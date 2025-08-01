package disease;

import agents.Person;

public class Disease {

	private static int CRE = 1;
	private int type;
	private int simIndex;


	public double getBaselineBetaValue(int facilityType){
		double acuteCareBeta = -1.0;
		double longTermAcuteCareBeta = -1.0;
		double nursingHomeBeta = -1.0;
		double nhReduction;

		if(type == getCRE()){
		    
			longTermAcuteCareBeta = 0.0615;
			acuteCareBeta = longTermAcuteCareBeta;
			nhReduction = 10.0;
			nursingHomeBeta = acuteCareBeta / nhReduction;
		}

		double betaVal = 0.0;

		if(facilityType == 0) betaVal = longTermAcuteCareBeta;
		else if(facilityType == 1) betaVal = acuteCareBeta;
		else if(facilityType == 2) betaVal = nursingHomeBeta;

		return betaVal;
	}

	public double getMeanTimeToClinicalDetection(int facilityType){
		double acuteCareMean = -1.0;
		double longTermAcuteCareMean = -1.0;
		double nursingHomeMean = -1.0;
		double nhChangeFactor = 1.0;


		if(type == getCRE()){
			acuteCareMean = 122.0;
			nhChangeFactor = 8.0;
			longTermAcuteCareMean = 106.0;
			nursingHomeMean = acuteCareMean * nhChangeFactor;
		}

		double t = 0.0;

		if(facilityType == 0) t = longTermAcuteCareMean;
		else if(facilityType == 1) t = acuteCareMean;
		else if(facilityType == 2) t = nursingHomeMean;

		return t;
	}


	public String getDiseaseName(){
		if(type==getCRE()) return "CRE";
		return "";
	}

	public double getAvgDecolonizationTime(){
		if(type == getCRE()) return 387.0;
		return 0;
	}

	public double getProbSurveillanceDetection(){
		return 0.8;
	}

	public boolean allowImportationsDuringBurnIn(){
		if(type == getCRE()) 
		    return false;
		return true;
	}

	public boolean isolatePatientWhenDetected(){
		if(type == getCRE()) return true;
		return false;
	}

	public boolean isActiveSurveillanceAgent(){
		if(type == getCRE()) return true;
		return false;
	}

	public double getImportationProb(){
		return 0.206;
	}
	public int getSimIndex() {
		return simIndex;
	}

	public void setType(int diseaseType) {
		type = diseaseType;
	}

	public static int getCRE() {
	    return CRE;
	}

	public static void setCRE(int cRE) {
	    CRE = cRE;
	}

	public void setSimIndex(int simIndex) {
	    this.simIndex = simIndex;
	}
}