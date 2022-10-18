package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileWindow extends JFrame implements ActionListener, Runnable {
    Thread compiler = null;
    Thread run_prom = null;
    boolean bn = true;
    CardLayout mycard;
    File file_saved = null;
    JButton button_input_text,
            button_compiler_text,
            button_compiler,
            button_run_prom,
            button_see_doswin;

    JPanel p = new JPanel();
    JTextArea input_text = new JTextArea();
    JTextArea compiler_text = new JTextArea();
    JTextArea dos_out_text = new JTextArea();

    JTextField input_file_name_text = new JTextField();
    JTextField run_file_name_text = new JTextField();

    public FileWindow() {
        super("Java语言编辑器");
        mycard = new CardLayout();
        compiler = new Thread(this);
        run_prom = new Thread(this);

        button_input_text = new JButton("程序输入区");
        button_compiler_text = new JButton("编译结果区");
        button_see_doswin = new JButton("程序运行结果");
        button_compiler = new JButton("编译程序");
        button_run_prom = new JButton("运行程序");

        p.setLayout(mycard);
        p.add("input", input_text);
        p.add("compiler", compiler_text);
        p.add("dos", dos_out_text);
        this.add(p, "Center");

        JPanel p1 = new JPanel();
        p1.setLayout(new GridLayout(3, 3));
        p1.add(button_input_text);
        p1.add(button_compiler_text);
        p1.add(button_see_doswin);
        p1.add(new JLabel("输入编译文件名（.java）："));
        p1.add(input_file_name_text);
        p1.add(button_compiler);
        p1.add(new JLabel("输入应用程序主类名："));
        p1.add(run_file_name_text);
        p1.add(button_run_prom);
        add(p1, "North");

        button_input_text.addActionListener(this);
        button_compiler.addActionListener(this);
        button_compiler_text.addActionListener(this);
        button_run_prom.addActionListener(this);
        button_see_doswin.addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button_input_text)
            mycard.show(p, "input");
        else if (e.getSource() == button_compiler_text)
            mycard.show(p, "compiler");
        else if (e.getSource() == button_see_doswin)
            mycard.show(p, "dos");
        else if (e.getSource() == button_compiler){
            if (!compiler.isAlive()){
                compiler = new Thread(this);
            }
            try {
                compiler.start();
            } catch (Exception e2){
                e2.printStackTrace();
            }
            mycard.show(p, "compiler");
        }
        else if (e.getSource() == button_run_prom){
            if(!run_prom.isAlive()){
                run_prom = new Thread(this);
            }
            try{
                run_prom.start();
            } catch (Exception e2){
                e2.printStackTrace();
            }
            mycard.show(p, "dos");
        }
    }

    public void run() {
        if (Thread.currentThread() == compiler){
            compiler_text.setText(null);
            String temp = input_text.getText().trim();
            byte [] buffer = temp.getBytes();
            int lens = buffer.length;
            String file_name = null;
            file_name = input_file_name_text.getText().trim();

            try {
                file_saved = new File(file_name);
                FileOutputStream writefile = new FileOutputStream(file_saved);
                writefile.write(buffer, 0, lens);
                writefile.close();
            } catch (Exception e) {
                System.out.println("ERROR");
            }

            try {
                Runtime rt=Runtime.getRuntime();
                InputStream in=rt.exec("javac "+file_name).getErrorStream(); //通过Runtime调用javac命令。注意：“javac ”这个字符串是有一个空格的！！

                BufferedInputStream bufIn=new BufferedInputStream(in);

                byte[] shuzu=new byte[100];
                int n=0;
                boolean flag=true;
                while((n=bufIn.read(shuzu, 0,shuzu.length))!=-1)
                {
                    String s=null;
                    s=new String(shuzu,0,n);
                    compiler_text.append(s);
                    if(s!=null)
                    {
                        flag=false;
                    }
                }
                //判断是否编译成功
                if(flag)
                {
                    compiler_text.append("Compile Succeed!");
                }
            } catch (Exception e) {
                // TODO: handle exception
                }
        }
        else if(Thread.currentThread()==run_prom)
        {
            //运行文件，并将结果输出到dos_out_text

            dos_out_text.setText(null);

            try {
                Runtime rt=Runtime.getRuntime();
                String path=run_file_name_text.getText().trim();
                Process stream=rt.exec("java "+path);//调用java命令

                InputStream in=stream.getInputStream();
                BufferedInputStream bisErr=new BufferedInputStream(stream.getErrorStream());
                BufferedInputStream bisIn=new BufferedInputStream(in);

                byte[] buf=new byte[150];
                byte[] err_buf=new byte[150];

                @SuppressWarnings("unused")
                int m=0;
                @SuppressWarnings("unused")
                int i=0;
                String s=null;
                String err=null;

                //打印编译信息及错误信息
                while((m=bisIn.read(buf, 0, 150))!=-1)
                {
                    s=new String(buf,0,150);
                    dos_out_text.append(s);
                }
                while((i=bisErr.read(err_buf))!=-1)
                {
                    err=new String(err_buf,0,150);
                    dos_out_text.append(err);
                }
            }
            catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
}

