package thito.nodeflow.internal;

import thito.nodeflow.api.*;
import thito.nodeflow.api.project.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class FacetClassLoader extends URLClassLoader {

    static {
        try {
//            java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod( "registerAsParallelCapable" );
//            if (method != null) {
//                method.setAccessible(true);
//                method.invoke(null);
//            }
            registerAsParallelCapable();
        } catch (Exception ex) {
        }
    }

    private Map<String, Class<?>> cached = new ConcurrentHashMap<>();
    public FacetClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    private ProjectFacet facetControl;

    public ProjectFacet getFacetControl() {
        return facetControl;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    private Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> cached = this.cached.get(name);
        if (cached != null) {
            return cached;
        }
        Class<?> bundle = NodeFlow.getApplication().getBundleManager().findClass(name);
        if (bundle != null) {
            cached = bundle;
        }
        // Facet are now standalone and should not be able to access other facet's class
//        if (checkGlobal) {
//            for (ProjectFacet other : ((ProjectManagerImpl) NodeFlow.getApplication().getProjectManager()).getRegisteredFacets()) {
//                if (other == facetControl) continue;
//                try {
//                    cached = ((FacetClassLoader) other.getClass().getClassLoader()).findClass(name, false);
//                    break;
//                } catch (Throwable t) {
//                }
//            }
//        }
        if (cached == null) {
            cached = super.findClass(name);
        }
        this.cached.put(name, cached);
        return cached;
    }

    protected void initialize(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> clazz = findClass(className);
        if (ProjectFacet.class.isAssignableFrom(clazz)) {
            facetControl = (ProjectFacet) clazz.newInstance();
            NodeFlow.getApplication().getProjectManager().registerFacet(facetControl);
            Toolkit.info("Loaded facet: "+facetControl.getId()+" ("+facetControl.getName()+")");
        }
    }

}
