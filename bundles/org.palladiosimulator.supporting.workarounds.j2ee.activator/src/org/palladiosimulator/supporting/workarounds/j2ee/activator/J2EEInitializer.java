package org.palladiosimulator.supporting.workarounds.j2ee.activator;

import org.eclipse.jst.j2ee.internal.J2EEInit;
import org.eclipse.ui.IStartup;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;

@SuppressWarnings("restriction")
public class J2EEInitializer implements IStartup {

    @Override
    public void earlyStartup() {
        /*
         * J2EE bundles do not initialize the provided EPackages on startup. This is problematic if
         * code such as OCL assumes that the global package registry is not changed during runtime.
         * As a workaround, we explicitly initialize the models. Otherwise,
         * ConcurrentModificationExceptions might occur that are pretty hard to debug. If we do not
         * use any of these web packages anymore, we can safely delete this whole bundle.
         */
        J2EEInit.init(true);
        J2EEInit.initEMFModels();
        ModulecorePlugin.getDefault();
    }

}
