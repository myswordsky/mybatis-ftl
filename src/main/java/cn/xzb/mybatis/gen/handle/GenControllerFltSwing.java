package cn.xzb.mybatis.gen.handle;

import cn.xzb.mybatis.gen.core.db.DataSourceConfig;
import cn.xzb.mybatis.gen.core.db.QueryMain;
import cn.xzb.mybatis.gen.core.db.domain.GlobalConfig;
import cn.xzb.mybatis.gen.core.db.domain.SQLUtils;
import cn.xzb.mybatis.gen.domain.ConfigProperties;
import cn.xzb.mybatis.gen.utils.BaseUtil;
import cn.xzb.mybatis.gen.utils.DebugUtil;
import cn.xzb.mybatis.gen.utils.FileBootUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码执行器
 */
public class GenControllerFltSwing extends JFrame {
    public static final String SUFFIX = "Suffix";
    public static final String CONFIG = "config";
    // 项目选择
    JComboBox<Object> selectProjectBox;
    // 表选择
    JComboBox<Object> selectTableBox;
    List<JCheckBox> checkBaseBoxList;
    /**
     * 生成按钮
     */
    JButton genBut;

    public static void main(String[] args) throws IOException {
        GenControllerFltSwing w = new GenControllerFltSwing();
        try {
            String lookAndFeel = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ClassPathResource resourceIco = new ClassPathResource("/ico/hatRSSblk.png");
        InputStream csv = resourceIco.getInputStream();
        ImageIcon imageIcon = new ImageIcon(FileBootUtil.toByteArray(csv));
        w.setIconImage(imageIcon.getImage());

        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置可关闭
        w.setAlwaysOnTop(true);             //总在最前面
        w.setSize(400, 400);//设置大小
        w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        w.setTitle("MybatisPlus代码生成器");
        //w.getContentPane().setLayout(new BorderLayout(0, 0));//流式自适应布局
        w.getContentPane().setLayout(new GridLayout(3, 20));//流式自适应布局
        w.getContentPane().setBounds(0, 0, 400, 400);
        w.initPanel(w.getContentPane());

        w.pack();
        w.setLocationRelativeTo(null);//设置居中
        w.setVisible(true);
    }


    public void initPanel(Container contentPane) {
        int height = 30;
        checkBaseBoxList = new ArrayList<>();
        selectProjectBox = new JComboBox<>();
        Map<String, ConfigProperties> map = new HashMap<>();
        String first = "";
        try {
            List<String> strings = listPropertiesFileUrl();

            for (String url : strings) {
                ConfigProperties config = ConfigProperties.buildProperties(url);
                String fileName = url.substring(url.lastIndexOf(File.separator) + 1);
                map.put(fileName, config);
                check(config, url);

                selectProjectBox.addItem(fileName);
            }
            first = strings.get(0).substring(strings.get(0).lastIndexOf(File.separator) + 1);

            selectProjectBox.setBackground(Color.orange);
            selectProjectBox.setSelectedItem(first);
            selectProjectBox.setFont(new Font("黑体", Font.PLAIN, 13));
            selectProjectBox.setBounds(0, 0, 120, height);
            selectProjectBox.addItemListener(e -> {
                Object item = e.getItem();
                selectTableBox.removeAllItems();
                checkBaseBoxList.forEach(contentPane::remove);
                checkBaseBoxList.clear();
                if (item == null) {
                    return;
                }
                try {
                    ConfigProperties configProperties = map.get(item.toString());
                    selectProjectBox.setToolTipText(JSON.toJSONString(configProperties));
                    addTableItems(contentPane, configProperties);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "配置异常:" + DebugUtil.printStack(ex), "失败", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            });
            contentPane.add(selectProjectBox);

            //表选择  监听
            selectTableBox = new JComboBox<>();
            selectTableBox.setBackground(Color.cyan);
            selectTableBox.setFont(new Font("黑体", Font.PLAIN, 13));
            selectTableBox.setBounds(130, 0, 120, height);
            contentPane.add(selectTableBox);
        } catch (Exception e) {
            System.err.println(DebugUtil.printStack(e));
            JOptionPane.showMessageDialog(this, "没有相关表或配置文件" + e.getMessage(), "失败", JOptionPane.INFORMATION_MESSAGE);
        }

        genBut = new JButton("♡((o(*生成文件*)o))♡");
        genBut.setBounds(260, 0, 120, height);
        genBut.setForeground(new Color(0xFFFFFF));
        genBut.setBackground(new Color(0xE02433));
        genBut.addActionListener(e11 -> {
            try {
                Object selectTableItem = selectTableBox.getSelectedItem();
                if (selectTableItem == null || StringUtils.isBlank(selectTableItem.toString())) {
                    JOptionPane.showMessageDialog(this, "error", "数据为空请输入", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Object selectedProjectItem = selectProjectBox.getSelectedItem();
                if (selectedProjectItem == null || StringUtils.isBlank(selectedProjectItem.toString())) {
                    JOptionPane.showMessageDialog(this, "error", "数据为空请输入", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                ConfigProperties configProperties = map.get(selectedProjectItem.toString());
                Properties ftlProperty = getFtlPropertyFileUrl(configProperties);
                String entityDir = ftlProperty.get(configProperties.getSqlEntity()).toString();
                entityDir = entityDir.endsWith(File.separator) ? entityDir.substring(0, entityDir.length() - 1) : entityDir;

                DataSourceConfig config = new DataSourceConfig.Builder(configProperties.getJdbc()
                        , configProperties.getUsername(),
                        configProperties.getPassword()).build();

                String tableName = selectTableItem.toString().trim();
                String entityName = SQLUtils.lowerLineToHump(tableName);

                GlobalConfig globalConfig = GlobalConfig.builder()
                        .entityDir(entityDir)
                        .author(configProperties.getAuthor())
                        .fileOverride(true)
                        .properties(configProperties)
                        .ftlProperty(ftlProperty)
                        .ftlInfoList(new ArrayList<>())
                        .build();
                for (JCheckBox box : checkBaseBoxList) {
                    if (box.isSelected()) {
                        String sourceSrc = box.getToolTipText();
                        String outputDir = box.getName();
                        String propertyKey = box.getText();

                        String fileName = FileBootUtil.getFileName(sourceSrc);
                        Object suffixObject = ftlProperty.get(fileName + SUFFIX);
                        String suffix = suffixObject == null ? "" : suffixObject.toString().trim();
                        String genFileName = entityName + suffix;
                        GlobalConfig.FtlInfo ftlInfo = GlobalConfig.FtlInfo.builder()
                                .sourceSrc(sourceSrc)
                                .outputDir(outputDir)
                                .genThisFileName(genFileName)
                                .ftlName(fileName)
                                .xml(propertyKey.endsWith("Xml")).build();
                        globalConfig.getFtlInfoList().add(ftlInfo);
                    }
                }
                QueryMain.gen(config, globalConfig, new String[]{tableName});
                JOptionPane.showMessageDialog(this, "success", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                System.err.println(DebugUtil.printStack(e1));
                JOptionPane.showMessageDialog(this, "error", DebugUtil.printStack(e1), JOptionPane.ERROR_MESSAGE);
            }
        });
        contentPane.add(genBut);

        try {
            addTableItems(contentPane, map.get(first));
        } catch (Exception e) {
            System.err.println(DebugUtil.printStack(e));
            JOptionPane.showMessageDialog(this, "添加组件异常" + e.getMessage(), "失败", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void check(ConfigProperties config, String url) throws IOException {
        String fileName = FileBootUtil.getFileName(url);

        Properties ftlProperty = getFtlPropertyFileUrl(config);
        for (Object ftlFile : ftlProperty.values()) {
            if (ftlFile == null || StringUtils.isBlank(ftlFile.toString())) {
                JOptionPane.showMessageDialog(this, "error"
                        , fileName + "的BaseOutputDir.properties未配置", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
        Set<Object> objects = ftlProperty.keySet();
        if (objects.size() == 0) {
            JOptionPane.showMessageDialog(this, "error"
                    , fileName + "的BaseOutputDir.properties未配置", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        // fileNames
        List<String> collect = objects.stream().map(Object::toString).collect(Collectors.toList());
        List<String> strings = listFtlFileUrl(config).stream().map(FileBootUtil::getFileName).collect(Collectors.toList());
        for (String str : collect) {
            if (str.endsWith(SUFFIX)) {
                continue;
            }
            if (!strings.contains(str)) {
                JOptionPane.showMessageDialog(this, "error"
                        , fileName + "的BaseOutputDir.properties配置的ftl文件不存在", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        }
    }

    public List<String> listPropertiesFileUrl() {
        String userDir = System.getProperty("user.dir");

        String url;

        if (BaseUtil.isJar()) {
            url = userDir + File.separator + CONFIG;
        } else {
            int i = userDir.lastIndexOf(File.separator);
            url = userDir.substring(0, i + 1) + CONFIG;
        }
        return FileBootUtil.listFileUrl(url, ".properties");
    }

    public List<String> listFtlFileUrl(ConfigProperties config) {
        String userDir = System.getProperty("user.dir");
        String url;
        String baseDir = CONFIG + File.separator + config.getDir();
        if (BaseUtil.isJar()) {
            url = userDir + File.separator + baseDir;
        } else {
            int i = userDir.lastIndexOf(File.separator);
            url = userDir.substring(0, i + 1) + baseDir;
        }
        return FileBootUtil.listFileUrl(url, ".ftl");
    }

    public Properties getFtlPropertyFileUrl(ConfigProperties config) throws IOException {
        String userDir = System.getProperty("user.dir");
        String url;
        String baseDir = CONFIG + File.separator + config.getDir();
        if (BaseUtil.isJar()) {
            url = userDir + File.separator + baseDir;
        } else {
            int i = userDir.lastIndexOf(File.separator);
            url = userDir.substring(0, i + 1) + baseDir;
        }
        // F:\Workspace\IDEA\IDEA\5git\config\ftl\BaseOutputDir.properties
        // F:\Workspace\IDEA\IDEA\5git\config\flt\BaseOutputDir.properties
        Properties properties = new Properties();
        properties.load(new FileInputStream(url + File.separator + "BaseOutputDir.properties"));
        return properties;
    }

    /**
     * 添加表列表、ftl选择框
     *
     * @param configProperties /
     */
    public void addTableItems(Container contentPane, ConfigProperties configProperties) throws IOException {
        DataSourceConfig config = new DataSourceConfig.Builder(configProperties.getJdbc()
                , configProperties.getUsername(),
                configProperties.getPassword()).build();
        List<String> listTableNames = QueryMain.listTableNames(config);
        for (String str : listTableNames) {
            selectTableBox.addItem(str);
        }
        selectTableBox.setSelectedItem(listTableNames.get(0));

        int index = 0;
        int checkWeight = 100;
        int height = 30;

        Properties ftlProperty = getFtlPropertyFileUrl(configProperties);
        List<String> strings = listFtlFileUrl(configProperties);
        Map<String, String> collect = strings.stream().collect(Collectors.toMap(FileBootUtil::getFileName, e -> e));
        for (Map.Entry<Object, Object> entry : ftlProperty.entrySet()) {
            String fileName = entry.getKey().toString();
            //排除SUFFIX
            if (fileName.endsWith(SUFFIX)) {
                continue;
            }
            String ftlSrc = collect.get(fileName);
            String outputDir = entry.getValue().toString();
            JCheckBox box = new JCheckBox();
            box.setBackground(new Color(0xFF1782CE, true));
            box.setText(fileName);
            box.setToolTipText(ftlSrc);
            box.setName(outputDir);
            box.setSelected(true);
            box.setFont(new Font("黑体", Font.PLAIN, 13));
            checkBaseBoxList.add(box);
            box.setBounds((index + 1) * (checkWeight + 10), height * 2 + 20, checkWeight, height);
            contentPane.add(box);
            index++;
        }
    }
}
