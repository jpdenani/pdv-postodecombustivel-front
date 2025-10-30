package br.com.frontend.util;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SwingWorkerUtils {

    /**
     * Executa uma tarefa em background e processa o resultado
     */
    public static <T> void executeAsync(
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Exception> onError
    ) {
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() throws Exception {
                return backgroundTask.get();
            }

            @Override
            protected void done() {
                try {
                    T result = get();
                    onSuccess.accept(result);
                } catch (Exception ex) {
                    onError.accept(ex);
                }
            }
        };
        worker.execute();
    }

    /**
     * Executa uma tarefa sem retorno
     */
    public static void executeAsync(
            Runnable backgroundTask,
            Runnable onSuccess,
            Consumer<Exception> onError
    ) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                backgroundTask.run();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    onSuccess.run();
                } catch (Exception ex) {
                    onError.accept(ex);
                }
            }
        };
        worker.execute();
    }
}
