package dunos.ui;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * DunDunDunOS AnimationEngine - Provides smooth animations for UI components.
 * Supports fade, slide, scale, and bounce animations with configurable timing
 * and easing functions.
 */
public class AnimationEngine {

    private static volatile AnimationEngine instance;
    private final Timer animationTimer;
    private final List<Animation> activeAnimations;
    private boolean enabled;

    /**
     * Easing functions for smooth animation curves.
     */
    public enum Easing {
        LINEAR,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT,
        BOUNCE,
        ELASTIC
    }

    /**
     * Types of animations supported.
     */
    public enum Type {
        FADE_IN,
        FADE_OUT,
        SLIDE_IN,
        SLIDE_OUT,
        SCALE_IN,
        SCALE_OUT,
        BOUNCE_IN,
        BOUNCE_OUT
    }

    /**
     * Abstract animation class representing a single animation instance.
     */
    public abstract static class Animation {
        protected final Component component;
        protected final long duration;
        protected final Easing easing;
        protected long startTime;
        protected boolean running;
        protected Consumer<Void> onComplete;

        Animation(Component component, long duration, Easing easing) {
            this.component = component;
            this.duration = duration;
            this.easing = easing;
            this.running = false;
        }

        public void setOnComplete(Consumer<Void> callback) {
            this.onComplete = callback;
        }

        public boolean isRunning() { return running; }

        public void start() {
            startTime = System.currentTimeMillis();
            running = true;
        }

        public void stop() {
            running = false;
            applyProgress(1.0);
            complete();
        }

        public boolean update() {
            if (!running) return false;
            long elapsed = System.currentTimeMillis() - startTime;
            double progress = Math.min(1.0, (double) elapsed / duration);
            double easedProgress = applyEasing(progress);
            applyProgress(easedProgress);
            if (progress >= 1.0) {
                running = false;
                complete();
                return false;
            }
            return true;
        }

        protected abstract void applyProgress(double progress);

        protected double applyEasing(double t) {
            return switch (easing) {
                case LINEAR -> t;
                case EASE_IN -> t * t;
                case EASE_OUT -> t * (2 - t);
                case EASE_IN_OUT -> t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
                case BOUNCE -> bounceEasing(t);
                case ELASTIC -> elasticEasing(t);
            };
        }

        private double bounceEasing(double t) {
            if (t < (1/2.75)) return 7.5625 * t * t;
            if (t < (2/2.75)) return 7.5625 * (t -= 1.5/2.75) * t + 0.75;
            if (t < (2.5/2.75)) return 7.5625 * (t -= 2.25/2.75) * t + 0.9375;
            return 7.5625 * (t -= 2.625/2.75) * t + 0.984375;
        }

        private double elasticEasing(double t) {
            if (t == 0 || t == 1) return t;
            return Math.pow(2, -10 * t) * Math.sin((t - 0.075) * (2 * Math.PI) / 0.3) + 1;
        }

        private void complete() {
            if (onComplete != null) {
                onComplete.accept(null);
            }
        }
    }

    /**
     * Fade animation for changing opacity.
     */
    public static class FadeAnimation extends Animation {
        private final float fromAlpha;
        private final float toAlpha;
        private boolean fadeIn;

        public FadeAnimation(Component component, long duration, Easing easing, boolean fadeIn) {
            super(component, duration, easing);
            this.fadeIn = fadeIn;
            this.fromAlpha = fadeIn ? 0.0f : 1.0f;
            this.toAlpha = fadeIn ? 1.0f : 0.0f;
        }

        @Override
        protected void applyProgress(double progress) {
            float alpha = (float) (fromAlpha + (toAlpha - fromAlpha) * progress);
            if (component instanceof JComponent) {
                ((JComponent) component).putClientProperty("alpha", alpha);
            }
            component.setBackground(new Color(
                component.getBackground().getRed(),
                component.getBackground().getGreen(),
                component.getBackground().getBlue(),
                (int) (alpha * 255)
            ));
            component.repaint();
        }
    }

    /**
     * Slide animation for moving components.
     */
    public static class SlideAnimation extends Animation {
        private final Point from;
        private final Point to;

        public SlideAnimation(Component component, long duration, Easing easing, 
                               Point from, Point to) {
            super(component, duration, easing);
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyProgress(double progress) {
            int x = (int) (from.x + (to.x - from.x) * progress);
            int y = (int) (from.y + (to.y - from.y) * progress);
            component.setLocation(x, y);
        }
    }

    /**
     * Scale animation for growing/shrinking components.
     */
    public static class ScaleAnimation extends Animation {
        private final double fromScale;
        private final double toScale;
        private final int originalWidth;
        private final int originalHeight;

        public ScaleAnimation(Component component, long duration, Easing easing,
                               double fromScale, double toScale) {
            super(component, duration, easing);
            this.fromScale = fromScale;
            this.toScale = toScale;
            this.originalWidth = component.getWidth();
            this.originalHeight = component.getHeight();
        }

        @Override
        protected void applyProgress(double progress) {
            double scale = fromScale + (toScale - fromScale) * progress;
            int w = (int) (originalWidth * scale);
            int h = (int) (originalHeight * scale);
            component.setSize(w, h);
            component.revalidate();
        }
    }

    private AnimationEngine() {
        this.activeAnimations = Collections.synchronizedList(new ArrayList<>());
        this.enabled = true;
        this.animationTimer = new Timer(16, e -> updateAnimations()); // ~60 FPS
    }

    /**
     * Gets the singleton AnimationEngine instance.
     */
    public static AnimationEngine getInstance() {
        if (instance == null) {
            synchronized (AnimationEngine.class) {
                if (instance == null) {
                    instance = new AnimationEngine();
                }
            }
        }
        return instance;
    }

    /**
     * Starts the animation engine.
     */
    public void start() {
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }

    /**
     * Stops the animation engine.
     */
    public void stop() {
        animationTimer.stop();
        activeAnimations.clear();
    }

    /**
     * Enables or disables animations.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Checks if animations are enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Plays a fade-in animation on a component.
     */
    public Animation fadeIn(Component component, long duration) {
        return play(new FadeAnimation(component, duration, Easing.EASE_OUT, true));
    }

    /**
     * Plays a fade-out animation on a component.
     */
    public Animation fadeOut(Component component, long duration) {
        return play(new FadeAnimation(component, duration, Easing.EASE_IN, false));
    }

    /**
     * Slides a component from one position to another.
     */
    public Animation slideTo(Component component, Point from, Point to, long duration) {
        return play(new SlideAnimation(component, duration, Easing.EASE_OUT, from, to));
    }

    /**
     * Scales a component.
     */
    public Animation scaleTo(Component component, double fromScale, double toScale, long duration) {
        return play(new ScaleAnimation(component, duration, Easing.EASE_OUT, fromScale, toScale));
    }

    /**
     * Plays a predefined animation type on a component.
     */
    public Animation playAnimation(Component component, Type type, long duration) {
        return switch (type) {
            case FADE_IN -> fadeIn(component, duration);
            case FADE_OUT -> fadeOut(component, duration);
            case SLIDE_IN -> slideTo(component, 
                new Point(component.getX() + 50, component.getY()),
                component.getLocation(), duration);
            case SLIDE_OUT -> slideTo(component,
                component.getLocation(),
                new Point(component.getX() + 50, component.getY()),
                duration);
            case SCALE_IN -> scaleTo(component, 0.5, 1.0, duration);
            case SCALE_OUT -> scaleTo(component, 1.0, 0.5, duration);
            case BOUNCE_IN -> play(new ScaleAnimation(component, duration, Easing.BOUNCE, 0.3, 1.0));
            case BOUNCE_OUT -> play(new ScaleAnimation(component, duration, Easing.BOUNCE, 1.0, 0.3));
        };
    }

    /**
     * Adds and starts an animation.
     */
    public Animation play(Animation animation) {
        if (enabled) {
            animation.start();
            activeAnimations.add(animation);
        } else {
            animation.applyProgress(1.0);
        }
        return animation;
    }

    /**
     * Removes all animations associated with a component.
     */
    public void cancelAnimations(Component component) {
        activeAnimations.removeIf(a -> a.component == component);
    }

    private void updateAnimations() {
        synchronized (activeAnimations) {
            Iterator<Animation> iterator = activeAnimations.iterator();
            while (iterator.hasNext()) {
                Animation anim = iterator.next();
                if (!anim.update()) {
                    iterator.remove();
                }
            }
        }
    }
}

