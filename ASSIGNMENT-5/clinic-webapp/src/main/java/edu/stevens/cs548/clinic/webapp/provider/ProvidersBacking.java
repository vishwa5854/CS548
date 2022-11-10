package edu.stevens.cs548.clinic.webapp.provider;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import edu.stevens.cs548.clinic.service.IProviderService;
import edu.stevens.cs548.clinic.service.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.dto.ProviderDto;
import edu.stevens.cs548.clinic.webapp.BaseBacking;

@Named("providersBacking")
@RequestScoped
public class ProvidersBacking extends BaseBacking {

	private static final long serialVersionUID = -733113325524128462L;
	
	// TODO
	@Inject
	IProviderService providerService;

	/*
	 * The list of providers, from which the cursor is selected.
	 */
	private List<ProviderDto> providers;

	public List<ProviderDto> getProviders() {
		return providers;
	}

	@PostConstruct
	private void init() {
		try {
			providers = providerService.getProviders();
		} catch (ProviderServiceExn e) {
			throw new IllegalStateException("Failed to get list of providers.", e);
		}
	}


}
