package task

import java.util.concurrent.Callable

interface ITask<V> extends Callable<V> {

}