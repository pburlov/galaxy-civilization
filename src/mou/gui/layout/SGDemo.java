package mou.gui.layout;

import java.awt.Color;
import java.awt.Container;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SGDemo extends JFrame
{

	String fileName;
	SGLayout layout = new SGLayout(3, 2, SGLayout.FILL, SGLayout.FILL, 15, 5);
	JButton saveButton = new JButton("Save");

	public SGDemo()
	{
		this("Untitled");
		setBounds(0, 0, 500, 400);
	}

	public SGDemo(String title)
	{
		setTitle(title);
		setBounds(0, 0, 380, 250);
		addContent(getContentPane());
		setVisible(true);
	}

	private void addContent(Container contentPane)
	{
		layout.setMargins(5, 10, 20, 40);
		layout.setColumnAlignment(0, SGLayout.RIGHT, SGLayout.TOP);
		layout.setAlignment(0, 1, SGLayout.LEFT, SGLayout.CENTER);
		layout.setAlignment(1, 0, SGLayout.RIGHT, SGLayout.FILL);
		layout.setAlignment(2, 0, SGLayout.FILL, SGLayout.BOTTOM);
		layout.setAlignment(2, 1, SGLayout.FILL, SGLayout.FILL);
		layout.setRowScale(1, 2.0);
		layout.setColumnScale(0, 0.65);
		contentPane.setLayout(layout); // or
		// contentPane.setLayout(new GridLayout(3, 2, 5, 0));
		JLabel label0 = new JLabel("RIGHT, TOP");
		label0.setBorder(BorderFactory.createRaisedBevelBorder());
		contentPane.add(label0);
		contentPane.add(new JTextField("LEFT, CENTER")); // or
		// contentPane.add(new JTextField(16)); // or
		// contentPane.add(new JTextField());
		JLabel label1 = new JLabel("RIGHT, FILL");
		label1.setBorder(BorderFactory.createRaisedBevelBorder());
		label1.setOpaque(true);
		contentPane.add(label1);
		JPanel buttonPanel = new JPanel();
		PointLayout buttonLayout = new PointLayout(PointLayout.RIGHT, PointLayout.CENTER);
		buttonLayout.setMargins(25, 10, 5, 40); // try commenting this line out
		buttonPanel.setLayout(buttonLayout);
		buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(saveButton);
		contentPane.add(buttonPanel);
		JLabel label2 = new JLabel("FILL, BOTTOM");
		label2.setBorder(BorderFactory.createRaisedBevelBorder());
		contentPane.add(label2);
		JPanel lastPanel = new JPanel();
		lastPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		PointLayout lastLayout = new PointLayout(PointLayout.CENTER, PointLayout.CENTER);
		lastPanel.setLayout(lastLayout); // try commenting this line out
		JLabel label = new JLabel("CENTER, CENTER in FILL, FILL");
		label.setBackground(Color.white);
		label.setOpaque(true);
		lastPanel.add(label);
		contentPane.add(lastPanel);
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	static public void main(String[] args)
	{
		String fileName = "C:\\FrameDemo.jpg";
		if(args.length > 0) fileName = args[0];
		SGDemo frame = new SGDemo("SGLayout demo");
		frame.setFileName(fileName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}