package gov.samhsa.consent2share.si;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import gov.samhsa.acs.audit.AuditService;
import gov.samhsa.acs.audit.PredicateKey;
import gov.samhsa.acs.common.tool.DocumentXmlConverter;
import gov.samhsa.acs.common.tool.SimpleMarshaller;
import gov.samhsa.acs.xdsb.common.XdsbDocumentType;
import gov.samhsa.acs.xdsb.repository.wsclient.adapter.XdsbRepositoryAdapter;
import gov.samhsa.acs.xdsb.repository.wsclient.exception.XdsbRepositoryAdapterException;
import gov.samhsa.consent2share.si.audit.SIAuditVerb;
import oasis.names.tc.ebxml_regrep.xsd.rs._3.RegistryResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class ConsentSignedMessageHandlerTest {

	private static final String SUCCESS = AbstractConsentMessageHandler.URN_RESPONSE_SUCCESS;
	private static final String FAIL = "FAIL";

	@Mock
	private ConsentGetter consentGetterMock;
	@Mock
	private XdsbRepositoryAdapter xdsbRepositoryMock;
	@Mock
	private DocumentXmlConverter documentXmlConverterMock;
	@Mock
	private AuditService auditServiceMock;
	@Mock
	private SimpleMarshaller marshallerMock;
	
	final String dataMock = "1";
	final String consentMock = "consentMock";
	final String domainIdMock = "domainIdMock";
	final String patientEidMock = "patientEidMock";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@InjectMocks
	private ConsentSignedMessageHandler sut;

	@Test
	public void testHandleMessage_Successes_Given_XDS_Returns_Success_Response() throws Throwable {
		// Arrange
		ReflectionTestUtils.setField(sut, "domainId", domainIdMock);
		SimpleConsentDto simpleConsentDtoMock = mock(SimpleConsentDto.class);
		RegistryResponse registryResponseMock = mock(RegistryResponse.class);
		doNothing().when(auditServiceMock).audit(eq(sut), anyString(),
				eq(SIAuditVerb.XDS_ADD_CONSENT), eq(patientEidMock),
				anyMapOf(PredicateKey.class, String.class));
		when(simpleConsentDtoMock.geteId()).thenReturn(patientEidMock);
		when(simpleConsentDtoMock.getConsent()).thenReturn(consentMock);
		when(consentGetterMock.getConsent(1)).thenReturn(simpleConsentDtoMock);

		when(
				xdsbRepositoryMock.provideAndRegisterDocumentSet(consentMock,
						domainIdMock, XdsbDocumentType.PRIVACY_CONSENT, null,
						null)).thenReturn(registryResponseMock);

		when(registryResponseMock.getStatus()).thenReturn(
				SUCCESS);

		// Act
		String response = sut.handleMessage(dataMock);

		// Assert
		assertEquals("Saved in XDS.b repository", response);
	}

	@Test
	public void testHandleMessage_Throws_Exception_Given_XDS_Returns_Fail_Response() throws Throwable {
		// Arrange
		ReflectionTestUtils.setField(sut, "domainId", domainIdMock);
		SimpleConsentDto simpleConsentDtoMock = mock(SimpleConsentDto.class);
		RegistryResponse registryResponseMock = mock(RegistryResponse.class);
		doNothing().when(auditServiceMock).audit(eq(sut), anyString(),
				eq(SIAuditVerb.XDS_ADD_CONSENT), eq(patientEidMock),
				anyMapOf(PredicateKey.class, String.class));
		when(simpleConsentDtoMock.geteId()).thenReturn(patientEidMock);
		when(simpleConsentDtoMock.getConsent()).thenReturn(consentMock);
		when(consentGetterMock.getConsent(1)).thenReturn(simpleConsentDtoMock);
		
		//failure 
		when(registryResponseMock.getRegistryErrorList()).thenReturn(null);
		
		when(
				xdsbRepositoryMock.provideAndRegisterDocumentSet(consentMock,
						domainIdMock, XdsbDocumentType.PRIVACY_CONSENT, null,
						null)).thenReturn(registryResponseMock);

		when(registryResponseMock.getStatus()).thenReturn(
				FAIL);
		
		try {
			// Act
			sut.handleMessage(dataMock);
		} catch (Throwable e) {
			String errorMessageExpected = "Failed to save in XDS.b repository becuase response status is not " + SUCCESS;
			// Assert
			assertEquals(errorMessageExpected, e.getMessage());
		}
	}

	@Test
	public void testHandleMessage_Throws_Exception_Given_XDS_Throws_Exception() throws Throwable {
		// Arrange
		ReflectionTestUtils.setField(sut, "domainId", domainIdMock);
		SimpleConsentDto simpleConsentDtoMock = mock(SimpleConsentDto.class);
		doNothing().when(auditServiceMock).audit(eq(sut), anyString(),
				eq(SIAuditVerb.XDS_ADD_CONSENT), eq(patientEidMock),
				anyMapOf(PredicateKey.class, String.class));
		when(simpleConsentDtoMock.geteId()).thenReturn(patientEidMock);
		when(simpleConsentDtoMock.getConsent()).thenReturn(consentMock);
		when(consentGetterMock.getConsent(1)).thenReturn(simpleConsentDtoMock);
		
		XdsbRepositoryAdapterException exMock = mock(XdsbRepositoryAdapterException.class);

		when(
				xdsbRepositoryMock.provideAndRegisterDocumentSet(consentMock,
						domainIdMock, XdsbDocumentType.PRIVACY_CONSENT, null,
						null)).thenThrow(exMock);
		try {
			// Act
			sut.handleMessage(dataMock);
		} catch (Throwable e) {
			// Assert
			assertEquals(exMock, e);
		}
	}
}
