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

/**
 * The Class PolicyIdDto.
 */
public class PolicyIdDto {

	/** The consent id. */
	private long consentId;

	/** The policy id. */
	private String policyId;

	/** The patient id. */
	private String patientEid;
	
	/** The patient id. */
	private long patientId;

	/**
	 * Gets the consent id.
	 * 
	 * @return the consent id
	 */
	public long getConsentId() {
		return consentId;
	}

	/**
	 * Sets the consent id.
	 * 
	 * @param consentId
	 *            the new consent id
	 */
	public void setConsentId(long consentId) {
		this.consentId = consentId;
	}

	/**
	 * Gets the policy id.
	 * 
	 * @return the policy id
	 */
	public String getPolicyId() {
		return policyId;
	}

	/**
	 * Sets the policy id.
	 * 
	 * @param policyId
	 *            the new policy id
	 */
	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	/**
	 * Gets the patient eid.
	 *
	 * @return the patient eid
	 */
	public String getPatientEid() {
		return patientEid;
	}

	/**
	 * Sets the patient eid.
	 *
	 * @param patientEid the new patient eid
	 */
	public void setPatientEid(String patientEid) {
		this.patientEid = patientEid;
	}

	/**
	 * Gets the patient id.
	 *
	 * @return the patient id
	 */
	public long getPatientId() {
		return patientId;
	}

	/**
	 * Sets the patient id.
	 *
	 * @param patientId the new patient id
	 */
	public void setPatientId(long patientId) {
		this.patientId = patientId;
	}	
}
