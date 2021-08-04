package thito.nodeflow.internal.node.search;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.eventbus.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.project.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;

import java.util.*;
import java.util.function.*;

public class NodeCategorySearchTool {
    private SimpleTaskQueue taskQueue = new SimpleTaskQueue(TaskThread.BACKGROUND);

    public void markReady() {
        taskQueue.markReady();
    }

    private Project project;

    public NodeCategorySearchTool(Project project) {
        this.project = project;
    }

    public void searchProvider(String search, NodeProviderCategory category, Consumer<List<NodeProvider>> consumer) {
        taskQueue.putQuery(() -> {
            Map<NodeProvider, Integer> scoreCache = new HashMap<>();
            for (NodeProvider provider : category.getProviders()) {
                if (provider instanceof MethodNodeProvider && ((MethodNodeProvider) provider).isImplementation()) continue;
                int providerScore = Toolkit.calculateSearchScore(provider.getID(), search) * 4 +
                        Toolkit.calculateSearchScore(provider.getDescription(), search) * 2 +
                        Toolkit.calculateSearchScore(provider.getName(), search) * 4;
                scoreCache.put(provider, providerScore);
            }
            List<NodeProvider> providers = new ArrayList<>(scoreCache.keySet());
            providers.sort(Comparator.comparingInt(scoreCache::get).reversed());
            consumer.accept(providers);
        });
    }

    public void search(String search, boolean eventsOnly, Consumer<Map<NodeProviderCategory, List<NodeProvider>>> consumer) {
        taskQueue.putQuery(() -> {
            Map<NodeProviderCategory, Integer> cache = new HashMap<>();
            Map<NodeProviderCategory, List<NodeProvider>> map = new LinkedHashMap<>();
            for (NodeProviderCategory category : ModuleManagerImpl.getInstance().getCategories()) {
                if (eventsOnly) {
                    if (!(category instanceof EventProviderCategory || category instanceof CommandNodeCategory)) continue;
                    if (category instanceof EventProviderCategory) {
                        if (!Objects.equals(((EventProviderCategory) category).getFacet(), project.getFacet())) {
                            continue;
                        }
                    }
                    if (category instanceof CommandNodeCategory) {
                        if (!Objects.equals(((CommandNodeCategory) category).getFacet(), project.getFacet())) {
                            continue;
                        }
                    }
                } else {
                    if (category instanceof EventProviderCategory || category instanceof CommandNodeCategory) continue;
                }
                int score = Toolkit.calculateSearchScore(category.getName(), search) * 3 +
                        Toolkit.calculateSearchScore(category.getAlias(), search, false) * 5;
                if (!(category instanceof JavaNodeProviderCategory)) {
                    score *= 2;
                }
                Map<NodeProvider, Integer> scoreCache = new HashMap<>();
                for (NodeProvider provider : category.getProviders()) {
                    if (provider instanceof MethodNodeProvider && ((MethodNodeProvider) provider).isImplementation()) continue;
                    int providerScore = Toolkit.calculateSearchScore(provider.getID(), search) * 4 +
                            Toolkit.calculateSearchScore(provider.getDescription(), search) * 2 +
                            Toolkit.calculateSearchScore(provider.getName(), search) * 4;
                    if (providerScore > 0 || search.isEmpty()) {
                        score += providerScore / 4;
                        scoreCache.put(provider, providerScore);
                    }
                }
                if (!(category instanceof JavaNodeProviderCategory)) {
                    score *= 2;
                }
                if (score > 0 || search.isEmpty()) {
                    List<NodeProvider> providers = new ArrayList<>(scoreCache.keySet());
                    providers.sort(Comparator.comparingInt(scoreCache::get).reversed());
                    map.put(category, providers);
                    cache.put(category, score);
                }
            }
            map = sortByValue(map, Comparator.<Map.Entry<NodeProviderCategory, List<NodeProvider>>>comparingInt(x -> cache.get(x.getKey())).reversed());
            consumer.accept(map);
        });
    }

    public static <K, V> Map<K, V> sortByValue(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(comparator);

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
