package org.palladiosimulator.supporting.workarounds.epackageinit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Descriptor;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.ui.IStartup;

public class EPackageRegistryInitializer implements IStartup {
	
	@Override
	public void earlyStartup() {
		initRegistry(EPackage.Registry.INSTANCE);
	}

	protected void initRegistry(Registry instance) {
		Set<EPackage> seenPackages = new HashSet<EPackage>();
		for (int i = 0; i < getMaximumNumberOfIterations(); ++i) {
			Collection<String> registryKeys = new ArrayList<>(instance.keySet());
			for (String registryKey : registryKeys) {
				Object registryValue = instance.get(registryKey);
				if (registryValue instanceof EPackage.Descriptor) {
					EPackage.Descriptor descriptor = (Descriptor) registryValue;
					registryValue = descriptor.getEPackage();
				}
				if (registryValue instanceof EPackage) {
					Queue<EPackage> packageQueue = new LinkedList<EPackage>();
					packageQueue.add((EPackage) registryValue);
					for (EPackage currentPackage = packageQueue.peek(); !packageQueue.isEmpty(); currentPackage = packageQueue.poll()) {
						if (seenPackages.contains(currentPackage)) {
							continue;
						}
						seenPackages.add(currentPackage);
						Optional.ofNullable(currentPackage.getESuperPackage()).ifPresent(packageQueue::add);
						packageQueue.addAll(currentPackage.getESubpackages());
					}
				}
			}
			if (registryKeys.size() == instance.size()) {
				return;
			}
		}
	}

	protected int getMaximumNumberOfIterations() {
		return 10;
	}
}
