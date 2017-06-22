package com.openfarmanager.android.core.bus;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Rx Java EventBus implementation
 *
 * @author Vlad Namashko
 */
public class RxBus {

    private RxBus() {}

    private static RxBus sInstance;

    public static RxBus getInstance() {
        if (sInstance == null) {
            sInstance = new RxBus();
        }

        return sInstance;
    }

    private final Subject<BusEvent> mBus = PublishSubject.create();

    public void postEvent(BusEvent event) {
        mBus.onNext(event);
    }

    public<E extends PanelEvent> Observable<E> observerFor(Class<E> eventClass, int panelLocation) {
        return mBus.ofType(eventClass).filter(e -> e.getPanelLocation() == panelLocation);
    }

    public<E extends BusEvent> Observable<E> observerFor(Class<E> eventClass) {
        return mBus.ofType(eventClass);
    }

}
