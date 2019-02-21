package my.jmeter.samplers;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class MyJavaSamplerDemo extends AbstractJavaSamplerClient {

	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		SampleResult result = new SampleResult();
		result.sampleStart();
		
		//business logic...
		String name = context.getParameter("Name");
		result.setSamplerData("Could you say hello to me? My name is " + name + ".");
		
		result.sampleEnd();
		
		//这里将Name参数值写入response中
		result.setSuccessful(true);
		String message = "Hello, " + name + "!";
		result.setResponseMessage("This is response message: " + message);
		result.setResponseData(new String("This is response data: " + message).getBytes());
		result.setResponseCodeOK();
		return result;
	}
	
	@Override
	public Arguments getDefaultParameters() {
        Arguments arguments = new Arguments();
        arguments.addArgument("Name", "World");
        return arguments;
    }

}
