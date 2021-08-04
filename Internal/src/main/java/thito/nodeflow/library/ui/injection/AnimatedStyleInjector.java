package thito.nodeflow.library.ui.injection;

import javafx.animation.*;
import javafx.beans.property.*;
import javafx.css.*;
import javafx.util.*;
import thito.nodeflow.internal.*;

import java.util.*;

@Deprecated
public class AnimatedStyleInjector {
    public static void injectTransitionStyle(Styleable dummyInstance) {
        Toolkit.reportErrorLater(() -> {
            List<CssMetaData<? extends Styleable,?>> list = Toolkit.makeItModifiable(dummyInstance.getCssMetaData());
            for (CssMetaData<? extends Styleable, ?> style : new ArrayList<>(list)) {
                inject(list, style);
            }
        });
    }

    public static void inject(List list, CssMetaData style) {
        if (style.getSubProperties() != null) {
            List subMetaData = Toolkit.makeItModifiable(style.getSubProperties());
            for (Object sub : new ArrayList<>(subMetaData)) {
                if (sub instanceof CssMetaData) {
                    inject(list, (CssMetaData) sub);
                }
            }
        }
        CssMetaData animationSpeed = StyleInjector.create("animation-speed-"+style.getProperty(), StyleConverter.getDurationConverter());
        CssMetaData animatedProperty = new CssMetaData("animated-"+style.getProperty(), style.getConverter()) {

            @Override
            public boolean isSettable(Styleable styleable) {
                StyleableProperty property = getStyleableProperty(styleable);
                return property instanceof Property && ((Property) property).isBound();
            }

            @Override
            public StyleableProperty getStyleableProperty(Styleable styleable) {
                StyleableProperty staticProperty = style.getStyleableProperty(styleable);
                if (staticProperty != null) {
                    return cachedAnimation.computeIfAbsent(staticProperty, key -> new CachedAnimation(this, key, animationSpeed.getStyleableProperty(styleable)));
                }
                return null;
            }

            public String toString() {
                return getProperty();
            };
        };

        list.add(animationSpeed);
        list.add(animatedProperty);
    }


    private static final Map<StyleableProperty, CachedAnimation> cachedAnimation = new WeakHashMap<>();

    public static class CachedAnimation extends SimpleStyleableObjectProperty {

        private Timeline currentAnimation;
        private StyleableProperty staticProperty;
        private StyleableProperty durationProperty;
        public CachedAnimation(CssMetaData metaData, StyleableProperty staticProperty, StyleableProperty durationProperty) {
            super(metaData);
            this.staticProperty = staticProperty;
            this.durationProperty = durationProperty;
        }

        @Override
        protected void invalidated() {
            if (currentAnimation != null) {
                currentAnimation.stop();
            }
            currentAnimation = new Timeline(
                    new KeyFrame((Duration) durationProperty.getValue(), new KeyValue(staticProperty, get()))
            );
            currentAnimation.play();
            super.invalidated();
        }
    }

}
