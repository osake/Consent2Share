package gov.samhsa.acs.pep.ws;

import gov.samhsa.acs.pep.PolicyTrying;
import gov.samhsa.acs.pep.exception.PolicyEnforcementPointException;
import gov.samhsa.acs.pep.ws.contract.TryPolicyPortType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class PolicyTryingServiceImpl.
 */
@javax.jws.WebService(
                      serviceName = "TryPolicyService",
                      portName = "TryPolicyServicePort",
                      targetNamespace = "http://acs.samhsa.gov/pep/ws/contract",
                      wsdlLocation = "classpath:TryPolicy.wsdl",
                      endpointInterface = "gov.samhsa.acs.pep.ws.contract.TryPolicyPortType")
                      
public class PolicyTryingServiceImpl implements TryPolicyPortType {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    /** The policy trying. */
    private PolicyTrying policyTrying;
    
    /**
     * Instantiates a new policy trying service impl.
     *
     * @param tryPolicy the try policy
     */
    public PolicyTryingServiceImpl(PolicyTrying tryPolicy ) {
		this.policyTrying = tryPolicy;
	}

    /* (non-Javadoc)
     * @see gov.samhsa.acs.pep.ws.contract.TryPolicyPortType#tryPolicy(java.lang.String, java.lang.String, java.lang.String)
     */
    public String tryPolicy(String c32Xml, String xacmlPolicy, String purposeOfUse) { 
    	logger.info("Executing operation tryPolicy");
        
        String segmentedC32 = null;
		try {
			segmentedC32 = policyTrying.tryPolicy(c32Xml, xacmlPolicy, purposeOfUse);
		} catch (PolicyEnforcementPointException e) {
			logger.debug(e.getMessage(), e);
			logger.error(e.getMessage());
		}
        return segmentedC32;
    }
}
