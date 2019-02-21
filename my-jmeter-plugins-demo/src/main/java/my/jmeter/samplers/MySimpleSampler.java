package my.jmeter.samplers;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;

public class MySimpleSampler extends AbstractSampler {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 123L;
	
	private static final String MY_SAMPLE_NAME = "MySimpleSampler.name";	//对应脚本中的property

	@Override
	public SampleResult sample(Entry e) {
		SampleResult result = new SampleResult();
		result.setSampleLabel(getName());
		String name = getInputName();
		
		result.sampleStart();
		result.setSamplerData("Could you say hello to me? My name is " + name + ".");
		
		result.sampleEnd();
		result.setSuccessful(true);
		String message = "Hello, " + name + "!";
		result.setResponseMessage("This is response message: " + message);
		result.setResponseData(new String("This is response data: " + message).getBytes());
		result.setResponseCodeOK();
		return result;
	}

	public String getInputName() {
		return this.getPropertyAsString(MY_SAMPLE_NAME);
	}
	
	public void setInputName(String name) {
		this.setProperty(MY_SAMPLE_NAME, name);
	}
}
