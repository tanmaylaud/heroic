package com.spotify.heroic.async;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Provide some common implementations of a callback.
 *
 * @author udoprog
 *
 * @param <T>
 *            The value type of the callback.
 */
abstract class AbstractCallback<T> implements Callback<T> {
    @Override
    public <C> Callback<T> reduce(List<Callback<C>> queries,
            final Reducer<C, T> reducer) {
        final CallbackReducer.Handle<C> handle = new CallbackReducer.Handle<C>() {
            @Override
            public void done(Collection<C> results,
                    Collection<Exception> errors,
                    Collection<CancelReason> cancelled) throws Exception {
                if (!AbstractCallback.this.isReady())
                    return;

                try {
                    AbstractCallback.this.resolve(reducer.resolved(results,
                            errors, cancelled));
                } catch (final Exception error) {
                    AbstractCallback.this.fail(error);
                }
            }
        };

        return register(new CallbackReducer<C>(queries, handle));
    }

    @Override
    public <C> Callback<T> reduce(List<Callback<C>> queries,
            final StreamReducer<C, T> reducer) {
        final CallbackStreamReducer.Handle<C> handle = new CallbackStreamReducer.Handle<C>() {
            @Override
            public void finish(Callback<C> callback, C result) throws Exception {
                reducer.resolved(callback, result);
            }

            @Override
            public void error(Callback<C> callback, Exception error)
                    throws Exception {
                reducer.failed(callback, error);
            }

            @Override
            public void cancel(Callback<C> callback, CancelReason reason)
                    throws Exception {
                reducer.cancelled(callback, reason);
            }

            @Override
            public void done(int successful, int failed, int cancelled)
                    throws Exception {
                if (!AbstractCallback.this.isReady())
                    return;

                try {
                    AbstractCallback.this.resolve(reducer.resolved(successful,
                            failed, cancelled));
                } catch (final Exception error) {
                    AbstractCallback.this.fail(error);
                }
            }
        };

        return register(new CallbackStreamReducer<C>(queries, handle));
    }

    @Override
    public <C> Callback<C> transform(final DeferredTransformer<T, C> transformer) {
        final Callback<C> callback = newCallback();

        register(new Handle<T>() {
            @Override
            public void cancelled(CancelReason reason) throws Exception {
                callback.cancel(reason);
            }

            @Override
            public void failed(Exception e) throws Exception {
                callback.fail(e);
            }

            @Override
            public void resolved(T result) throws Exception {
                final Callback<C> transform = transformer.transform(result);

                callback.register(new Callback.Handle<C>() {
                    @Override
                    public void cancelled(CancelReason reason) throws Exception {
                        transform.cancel(reason);
                    }

                    @Override
                    public void failed(Exception e) throws Exception {
                        transform.fail(e);
                    }

                    @Override
                    public void resolved(C result) throws Exception {
                        transform.resolve(result);
                    }
                });

                transform.register(new Callback.Handle<C>() {
                    @Override
                    public void cancelled(CancelReason reason) throws Exception {
                        callback.cancel(reason);
                    }

                    @Override
                    public void failed(Exception e) throws Exception {
                        callback.fail(e);
                    }

                    @Override
                    public void resolved(C result) throws Exception {
                        callback.resolve(result);
                    }
                });
            }
        });

        callback.register(new Callback.Handle<C>() {
            @Override
            public void cancelled(CancelReason reason) throws Exception {
                AbstractCallback.this.cancel(reason);
            }

            @Override
            public void failed(Exception e) throws Exception {
                AbstractCallback.this.fail(e);
            }

            @Override
            public void resolved(C result) throws Exception {
            }
        });

        return callback;
    }

    @Override
    public <C> Callback<C> transform(final Transformer<T, C> transformer) {
        final Callback<C> callback = newCallback();

        register(new Handle<T>() {
            @Override
            public void cancelled(CancelReason reason) throws Exception {
                callback.cancel(reason);
            }

            @Override
            public void failed(Exception e) throws Exception {
                callback.fail(e);
            }

            @Override
            public void resolved(T result) throws Exception {
                try {
                    callback.resolve(transformer.transform(result));
                } catch (final Exception t) {
                    callback.fail(t);
                }
            }
        });

        callback.register(new Callback.Handle<C>() {
            @Override
            public void cancelled(CancelReason reason) throws Exception {
                AbstractCallback.this.cancel(reason);
            }

            @Override
            public void failed(Exception e) throws Exception {
                AbstractCallback.this.fail(e);
            }

            @Override
            public void resolved(C result) throws Exception {
            }
        });

        return callback;
    }

    @Override
    public Callback<T> resolve(final Executor executor,
            final Resolver<T> resolver) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!AbstractCallback.this.isReady())
                    return;

                try {
                    AbstractCallback.this.resolve(resolver.resolve());
                } catch (final Exception error) {
                    AbstractCallback.this.fail(error);
                }
            }
        };

        try {
            executor.execute(runnable);
        } catch (final Exception e) {
            AbstractCallback.this.fail(e);
        }

        return this;
    }

    @Override
    public Callback<T> register(final Callback<T> callback) {
        register(new Handle<T>() {
            @Override
            public void cancelled(CancelReason reason) throws Exception {
                callback.cancel(reason);
            }

            @Override
            public void failed(Exception e) throws Exception {
                callback.fail(e);
            }

            @Override
            public void resolved(T result) throws Exception {
                callback.resolve(result);
            }
        });

        callback.register(new Handle<T>() {
            @Override
            public void cancelled(CancelReason reason) throws Exception {
                AbstractCallback.this.cancel(reason);
            }

            @Override
            public void failed(Exception e) throws Exception {
                AbstractCallback.this.fail(e);
            }

            @Override
            public void resolved(T result) throws Exception {
                AbstractCallback.this.resolve(result);
            }
        });

        return this;
    }

    protected abstract <C> Callback<C> newCallback();
}
