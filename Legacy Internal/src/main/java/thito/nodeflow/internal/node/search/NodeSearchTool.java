package thito.nodeflow.internal.node.search;

import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodejfx.parameter.type.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

public class NodeSearchTool {
    private SimpleTaskQueue queue = new SimpleTaskQueue(TaskThread.BACKGROUND);

    public static boolean isTopLevelMethod(ExpectingType type, Method method) {
        Method found = type.getMethod(method.getName(), method.getParameterTypes());
        if (found != null) {
            return found.getDeclaringClass().isAssignableFrom(method.getDeclaringClass());
        } else {
        }
        return true;
    }

    public void search(String searchText, boolean input, ExpectingType expectedType, boolean implementation, Consumer<List<NodeProvider>> consumer) {
        queue.putQuery(() -> {
            Map<NodeProvider, Integer> providerMap = new HashMap<>();
            for (NodeProviderCategory category : ModuleManagerImpl.getInstance().getCategories()) {
                if (implementation) {
                    if (!(category instanceof JavaNodeProviderCategory)) continue;
                    JavaNodeProviderCategory javaCategory = (JavaNodeProviderCategory) category;
                    if (!expectedType.isAssignableTo(javaCategory.getType())) {
                        continue;
                    }
                    for (NodeProvider provider : category.getProviders()) {
                        if (provider instanceof MethodNodeProvider) {
                            MethodNodeProvider methodProvider = (MethodNodeProvider) provider;
                            if (methodProvider.isImplementation()) {
                                Method method = methodProvider.getMethod();
                                if (Modifier.isStatic(method.getModifiers())) continue;
                                if (!isTopLevelMethod(expectedType, method)) {
                                    continue;
                                }
                                int score = calculateSearchScore(provider, searchText, input, expectedType);
                                providerMap.put(provider, score);
                            }
                        }
                    }
                } else {
                    for (NodeProvider provider : category.getProviders()) {
                        if (provider instanceof MethodNodeProvider) {
                            MethodNodeProvider methodProvider = (MethodNodeProvider) provider;
                            if (!methodProvider.isImplementation()) {
                                Method method = methodProvider.getMethod();
                                if (!Modifier.isStatic(method.getModifiers()) && !isTopLevelMethod(expectedType, method)) {
                                    continue;
                                }
                            } else continue;
                        }
                        int score = calculateSearchScore(provider, searchText, input, expectedType);
                        if (score > 0 || searchText.isEmpty()) {
                            providerMap.put(provider, score);
                        }
                    }
                }
            }
            List<NodeProvider> providers = new ArrayList<>(providerMap.keySet());
            providers.sort(Comparator.comparingInt(providerMap::get).reversed());
            consumer.accept(providers);
        });
    }

    public void markReady() {
        queue.markReady();
    }

    public static int calculateSearchScore(NodeProvider provider, String search, boolean input, ExpectingType expectedType) {
        int score = 0;
        for (NodeParameterFactory param : ((AbstractNodeProvider) provider).getParameters()) {
            if (input && param.getInputMode() != LinkMode.NONE) {
                if (param.getType() == null) {
                    if (expectedType == null || expectedType.getClasses().isEmpty()) {
                        score += 2000;
                    }
                } else if (expectedType.getClasses().contains(param.getType())) {
                    score += 2000;
                } else if (expectedType.isAssignableTo(param.getType())) {
                    score += 10;
                } else if (expectedType.isAssignableFrom(param.getType())) {
                    score += 1000;
                }
            } else if (!input && param.getOutputMode() != LinkMode.NONE) {
                if (param.getType() == null) {
                    if (expectedType == null || expectedType.getClasses().isEmpty()) {
                        score += 2000;
                    }
                } else if (expectedType.getClasses().contains(param.getType())) {
                    score += 2000;
                } else if (expectedType.isAssignableTo(param.getType())) {
                    score += 1000;
                } else if (expectedType.isAssignableFrom(param.getType())) {
                    score += 10;
                }
            }
        }
        if (score > 0 && !search.isEmpty()) {
//            score = 0;
            score += (Toolkit.calculateSearchScore(provider.getID(), search) * 4 +
                    Toolkit.calculateSearchScore(provider.getName(), search) * 2 +
                    Toolkit.calculateSearchScore(provider.getDescription(), search)) * 100;
            if (provider.getCategory() instanceof JavaNodeProviderCategory) {
                score += Toolkit.calculateSearchScore(provider.getCategory().getName(), search) * 2;
                score += Toolkit.calculateSearchScore(provider.getCategory().getAlias(), search);
                Class<?> categoryType = ((JavaNodeProviderCategory) provider.getCategory()).getType();
                if (expectedType.getClasses().contains(categoryType)) {
                    score += 5000 * 3;
                } else if (expectedType.isAssignableTo(categoryType)) {
                    score += 2500 * 3;
                } else if (expectedType.isAssignableFrom(categoryType)) {
                    score += 1250 * 3;
                }
            } else {
                score += 100000;
            }
            if (provider instanceof ClassMemberProvider) {
                if (!Modifier.isStatic(((ClassMemberProvider) provider).getMember().getModifiers())) score += 15000;
            }
        }
        return score;
    }


}
