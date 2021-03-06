/*******************************************************************************
 * Open Behavioral Health Information Technology Architecture (OBHITA.org)
 *
 *   Redistribution and use in source and binary forms, with or without
 *   modification, are permitted provided that the following conditions are met:
 *       * Redistributions of source code must retain the above copyright
 *         notice, this list of conditions and the following disclaimer.
 *       * Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *       * Neither the name of the <organization> nor the
 *         names of its contributors may be used to endorse or promote products
 *         derived from this software without specific prior written permission.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *   ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *   WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *   DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *   DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *   (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *   (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *   SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package gov.samhsa.consent2share.si;

import static gov.samhsa.consent2share.si.audit.SIAuditVerb.*;
import static gov.samhsa.consent2share.si.audit.SIPredicateKey.*;

import java.util.Map;
import java.util.UUID;

import gov.samhsa.acs.audit.PredicateKey;
import gov.samhsa.acs.common.tool.DocumentXmlConverter;
import gov.samhsa.acs.common.tool.exception.SimpleMarshallerException;
import gov.samhsa.acs.xdsb.common.XdsbDocumentType;
import gov.samhsa.acs.xdsb.repository.wsclient.adapter.XdsbRepositoryAdapter;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ch.qos.logback.audit.AuditException;

/**
 * The Class ConsentSignedMessageHandler.
 */
public class ConsentSignedMessageHandler extends AbstractConsentMessageHandler {

	/** The xdsb repository. */
	@Autowired
	private XdsbRepositoryAdapter xdsbRepository;

	/** The document xml converter. */
	@Autowired
	private DocumentXmlConverter documentXmlConverter;

	/** The control bus service. */
	@Autowired
	private BusController controlBusService;

	/** The notification publisher. */
	@Autowired
	private NotificationPublisher notificationPublisher;

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * gov.samhsa.consent2share.si.AbstractConsentMessageHandler#handleMessage
	 * (java.lang.String)
	 */
	@Override
	public String handleMessage(String data) throws Throwable {
		String messageId = UUID.randomUUID().toString();
		logger.debug("Consent Signed Message Received: ConsentId"
				+ new String(data));

		Long consentId = Long.parseLong(data);

		// Get consent
		SimpleConsentDto consentDto = consentGetter.getConsent(consentId);

		// Save to XDS.b repository
		RegistryResponse response = null;
		try {
			response = xdsbRepository.provideAndRegisterDocumentSet(
					consentDto.getConsent(), domainId,
					XdsbDocumentType.PRIVACY_CONSENT, null, null);
			audit(messageId, consentDto, response);
		} catch (Throwable e) {
			logger.error("Failed to save in xds.b repository", e);

			throw e;
		}

		if (!URN_RESPONSE_SUCCESS.equals(response.getStatus())) {
			String errorMessage = "Failed to save in XDS.b repository becuase response status is not "
					+ URN_RESPONSE_SUCCESS;
			if (response.getRegistryErrorList() != null)
				logger.error(response.getRegistryErrorList().getRegistryError()
						.get(0).getCodeContext());
			logger.error(errorMessage);

			throw new Exception(errorMessage);
		}

		return "Saved in XDS.b repository";
	}

	/**
	 * Audit.
	 * 
	 * @param messageId
	 *            the message id
	 * @param consentDto
	 *            the consent dto
	 * @param registryResponse
	 *            the registry response
	 * @throws AuditException
	 *             the audit exception
	 */
	private void audit(String messageId, SimpleConsentDto consentDto,
			RegistryResponse registryResponse) throws AuditException {
		Map<PredicateKey, String> predicateMap = auditService
				.createPredicateMap();
		predicateMap.put(C2S_CONSENT_ID, consentDto.getConsentId());
		predicateMap.put(C2S_PATIENT_ID,
				Long.toString(consentDto.getPatientId()));
		predicateMap.put(DOMAIN_ID, domainId);
		if (registryResponse != null) {
			predicateMap.put(RESPONSE_STATUS, registryResponse.getStatus());
			try {
				predicateMap.put(RESPONSE_BODY,
						marshaller.marshal(registryResponse));
			} catch (SimpleMarshallerException e) {
				throw new AuditException(e.getMessage(), e);
			}
		}
		predicateMap.put(XACML_POLICY_ID, consentDto.getXacmlPolicyId());
		predicateMap.put(XACML_POLICY, consentDto.getConsent());
		auditService.audit(this, messageId, XDS_ADD_CONSENT,
				consentDto.geteId(), predicateMap);
	}
}
