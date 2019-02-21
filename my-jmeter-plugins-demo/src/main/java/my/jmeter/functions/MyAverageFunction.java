package my.jmeter.functions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.jmeter.engine.util.CompoundVariable;
import org.apache.jmeter.functions.AbstractFunction;
import org.apache.jmeter.functions.InvalidVariableException;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterVariables;

public class MyAverageFunction extends AbstractFunction {
	
	private static final String KEY = "__myAverage";
	
	private static final List<String> desc = new LinkedList<>();
	
	static {
        desc.add("第一个整数");
        desc.add("第二个整数(通过添加更多的参数来增加更多的整数)");
        desc.add("存储结果的变量名(可选)");
    }

    private Object[] values;

	@Override
	public List<String> getArgumentDesc() {
		return desc;
	}

	@Override
	public String execute(SampleResult previousResult, Sampler currentSampler) throws InvalidVariableException {
		int sum = 0;
		int count = 0;
        String varName = ((CompoundVariable) values[values.length - 1]).execute().trim();

        //对除最后一个参数外的其他参数求和
        for (int i = 0; i < values.length - 1; i++) {
            sum += Integer.parseInt(((CompoundVariable) values[i]).execute());
            count++;
        }

        //处理最后一个参数
        try {
            sum += Integer.parseInt(varName);	//最后一个参数仍为整数
            count++;
            varName = null; // there is no variable name
        } catch(NumberFormatException ignored) {
            // varName keeps its value and sum has not taken 
            // into account non numeric or overflowing number
        }
        
        int avg = sum/count;
        String result = String.valueOf(avg);
        
        //最后一个参数为变量名，保存为JMeter的变量
        JMeterVariables vars = getVariables();
        if (vars != null && varName != null){// vars will be null on TestPlan
            vars.put(varName.trim(), result);
        }

        return result;
	}

	@Override
	public void setParameters(Collection<CompoundVariable> parameters) throws InvalidVariableException {
		checkMinParameterCount(parameters, 2);
		values = parameters.toArray();
	}

	@Override
	public String getReferenceKey() {
		return KEY;
	}

}
