package my.jmeter.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;

import my.jmeter.samplers.MySimpleSampler;

public class MySimpleSamplerGui extends AbstractSamplerGui {

	/**
	 * 
	 */
	private static final long serialVersionUID = 124L;
	
	private JLabeledTextField nameField = new JLabeledTextField("Input your name: ");
	private static final String DEFAULT_NAME = "World";
	
	public MySimpleSamplerGui() {
		init();
	}
	
	private void init() {
		setLayout(new BorderLayout());
		setBorder(makeBorder());
		
		add(makeTitlePanel(), BorderLayout.NORTH);
		JPanel mainPanel = new JPanel();
		mainPanel.add(nameField);
		add(mainPanel, BorderLayout.CENTER);
	}

	@Override
	public String getLabelResource() {
		return null;
	}
	
	@Override
	public String getStaticLabel() {
		return "My Simple Sampler";
	}
	
	//新生成的GUI从TestElement中获取数据
	@Override
	public void configure(TestElement element) {
        super.configure(element);
        MySimpleSampler sampler = (MySimpleSampler) element;
        nameField.setText(sampler.getInputName());
    }

	//在GUI组件中生成TestElement的时候，将数据从GUI传递给TestElement
	@Override
	public TestElement createTestElement() {
		MySimpleSampler element = new MySimpleSampler();
		modifyTestElement(element);
		return element;
	}

	//给TestElement设置值
	@Override
	public void modifyTestElement(TestElement element) {
		MySimpleSampler sampler = (MySimpleSampler) element;
		sampler.setInputName(nameField.getText());
		super.configureTestElement(sampler);	//This method will set the name, gui class, and test class for the created Test Element
	}
	
	//重置
	@Override
	public void clearGui() {
		super.clearGui();
		nameField.setText(DEFAULT_NAME);
	}

}
