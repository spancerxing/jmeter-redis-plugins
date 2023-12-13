package com.jmeter.plugins;

import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * @author spancer
 */
public class RedisSamplerGui extends AbstractSamplerGui {

    private JTextField server;

    private JTextField password;

    private JComboBox<String> operation;

    private JTextField key;

    private JTextField expired;

    private JTextArea value;

    private JComboBox<String> type;

    public RedisSamplerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        JPanel panel = new VerticalPanel(7, 0);
        panel.add(getServer());
        panel.add(getPassword());
        panel.add(getOperation());
        panel.add(getKey());
        panel.add(getValue());
        panel.add(getType());
        panel.add(getExpired());
        add(panel, BorderLayout.CENTER);
    }

    private JPanel getServer() {
        server = new JTextField(15);
        JLabel label = new JLabel("Server:");
        label.setLabelFor(server);
        JPanel panel = new JPanel(new BorderLayout(34, 5));
        panel.add(label, BorderLayout.WEST);
        panel.add(server, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getPassword() {
        password = new JTextField(15);
        JLabel label = new JLabel("Password:");
        label.setLabelFor(password);
        JPanel panel = new JPanel(new BorderLayout(16, 5));
        panel.add(label, BorderLayout.WEST);
        panel.add(password, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getOperation() {
        operation = new JComboBox<>();
        operation.addItem("GET");
        operation.addItem("SET");
        JLabel label = new JLabel("Operation:");
        label.setLabelFor(operation);
        JPanel panel = new JPanel(new BorderLayout(13, 5));
        panel.add(label, BorderLayout.WEST);
        panel.add(operation, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getKey() {
        key = new JTextField(15);
        JLabel label = new JLabel("Key:");
        label.setLabelFor(key);
        JPanel panel = new JPanel(new BorderLayout(47, 5));
        panel.add(label, BorderLayout.WEST);
        panel.add(key, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getValue() {
        value = new JTextArea(5, 10);
        JLabel label = new JLabel("Value:");
        label.setLabelFor(value);
        JPanel panel = new JPanel(new BorderLayout(37, 5));
        panel.add(label, BorderLayout.WEST);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getType() {
        type = new JComboBox<>();
        type.addItem("String");
        type.addItem("Hash");
        type.addItem("List");
        type.addItem("Set");
        JLabel label = new JLabel("Type:");
        label.setLabelFor(type);
        JPanel panel = new JPanel(new BorderLayout(41, 5));
        panel.add(label, BorderLayout.WEST);
        panel.add(type, BorderLayout.CENTER);
        return panel;
    }

    private JPanel getExpired() {
        expired = new JTextField(15);
        JLabel label = new JLabel("Expired(s):");
        label.setLabelFor(expired);
        JPanel panel = new JPanel(new BorderLayout(16, 5));
        panel.add(label, BorderLayout.WEST);
        panel.add(expired, BorderLayout.CENTER);
        return panel;
    }

    @Override
    public String getStaticLabel() {
        return "Redis Sampler";
    }

    @Override
    public String getLabelResource() {
        throw new IllegalStateException("this method should not be called");
    }

    @Override
    public TestElement createTestElement() {
        RedisSampler sampler = new RedisSampler();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement testElement) {
        testElement.clear();
        super.configureTestElement(testElement);
        if (testElement instanceof RedisSampler) {
            testElement.setProperty("server", server.getText());
            testElement.setProperty("password", password.getText());
            testElement.setProperty("operation", (String) operation.getSelectedItem());
            testElement.setProperty("key", key.getText());
            testElement.setProperty("value", value.getText());
            testElement.setProperty("type", (String) type.getSelectedItem());
            testElement.setProperty("expired", expired.getText());
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        server.setText(element.getPropertyAsString("server"));
        password.setText(element.getPropertyAsString("password"));
        operation.setSelectedItem(element.getPropertyAsString("operation"));
        key.setText(element.getPropertyAsString("key"));
        value.setText(element.getPropertyAsString("value"));
        type.setSelectedItem(element.getPropertyAsString("type"));
        expired.setText(element.getPropertyAsString("expired"));
    }
}
