package com.openfarmanager.android.core.bus;

/**
 * @author Vlad Namashko
 */
public abstract class PanelEvent implements BusEvent {

    protected int mPanelLocation;

    public PanelEvent(int forPanel) {
        mPanelLocation = forPanel;
    }

    public int getPanelLocation() {
        return mPanelLocation;
    }
}
