import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.color.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Client
{
	private static final String secretkey = "semesterlabwork";
    private static final String saltvalue = "networkingincomp";
    private static Cipher encrypt;
    private static Cipher decrypt;
    final static File[] file = new File[1];
    static JFileChooser jfilechooser;
	private static JLabel cnt;
	private static JTextArea area;
    private static String ipaddr;
	private static Socket socket;

	public static void main(String[] args) throws Exception
	{
		JFrame frame = new JFrame("Client");
		frame.setSize(650 ,750);
		frame.setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(Color.BLACK);
		
		JLabel title = new JLabel("SECURE VIDEO CRYPTOGRAPHY :)");
		title.setFont(new Font("Times New Roman",Font.BOLD,30));
		title.setBorder(new EmptyBorder(20,0,10,0));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
		
		JLabel filename = new JLabel("Choose a file: ");
		filename.setFont(new Font("Times New Roman",Font.BOLD,25));
		filename.setBorder(new EmptyBorder(50,0,0,0));
		filename.setAlignmentX(Component.CENTER_ALIGNMENT);
        filename.setForeground(Color.WHITE);
		
		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(75,0,10,0));
		buttons.setBackground(Color.black);

		//JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		JButton sendfile = new JButton("Send File");
		sendfile.setPreferredSize(new Dimension(150,75));
		sendfile.setFont(new Font("Times New Roman", Font.BOLD, 20));
		sendfile.setFocusable(false);
		sendfile.setAlignmentY(Component.TOP_ALIGNMENT);
		
		JButton choosefile = new JButton("Choose File");
		choosefile.setPreferredSize(new Dimension(150,75));
		choosefile.setFont(new Font("Times New Roman", Font.BOLD, 20));
		choosefile.setFocusable(false);
		
		buttons.add(sendfile);
		buttons.add(choosefile);
	
		cnt = new JLabel("Connection History : ");
		cnt.setFont(new Font("Times New Roman",Font.BOLD,25));
		cnt.setBorder(new EmptyBorder(20,0,10,0));
		cnt.setAlignmentX(Component.CENTER_ALIGNMENT);
		cnt.setForeground(Color.WHITE);

        JLabel dev = new JLabel("Developed by:  Lokeshwar. S");
        dev.setFont(new Font("Times New Roman",Font.BOLD,25));
		dev.setBorder(new EmptyBorder(20,0,10,0));
        dev.setAlignmentX(Component.CENTER_ALIGNMENT);
		dev.setForeground(Color.WHITE);

		area = new JTextArea();
		area.setPreferredSize(new Dimension(250,250)); 
		area.setFont(new Font("Calibri",Font.BOLD,20));
		area.setEditable(false); 
				
		JButton cl=new JButton("Close");
		cl.setFont(new Font("Times New Roman",Font.BOLD,20));
		cl.setFocusable(false);
		cl.setAlignmentX(Component.CENTER_ALIGNMENT);
		cl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try{
				socket.close();
				frame.dispose();
				}catch(Exception ex)
				{}			
			}
		});
		frame.add(title);
		frame.add(filename);
		frame.add(buttons);
		frame.add(cnt);
        frame.add(area); 
        frame.add(dev);
		frame.add(cl);
		//frame.setContentPane(scroll);
		
		frame.setVisible(true);
		final String ipaddr[] = new String[1];
		ipaddr[0] = JOptionPane.showInputDialog(frame, "Enter the IP address of Server: ");
		while(ipaddr[0].equals(""))
		{
			JOptionPane.showMessageDialog(frame, "Please enter the IP address to connect !");
			ipaddr[0] = JOptionPane.showInputDialog(frame, "Enter the IP address of Server: ");
		}

		choosefile.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) 
					{
						jfilechooser = new JFileChooser();
						jfilechooser.setDialogTitle("Choose an Video File to Send : ");
						jfilechooser.setCurrentDirectory(new File("."));
						
						if(jfilechooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
						{
							file[0] = jfilechooser.getSelectedFile();
							filename.setText("The Selected File : "+file[0].getName());

						}
					}
				});

		sendfile.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						if(file[0] == null)
						{
							JOptionPane.showMessageDialog(frame, "Please Select a File !");
							filename.setText("No file selected !");
						}
						else
						{
							
							try
							{
								encryptfile();
								FileInputStream fileinputstream = new FileInputStream(file[0].getAbsolutePath());
								socket = new Socket(ipaddr[0], 1500);
								
								DataOutputStream dataoutputstream = new DataOutputStream(socket.getOutputStream());
								
								String filename = file[0].getName();
								byte[] filenamebytes = filename.getBytes();
								
								byte[] filecontentbytes = new byte[(int)file[0].length()];
								fileinputstream.read(filecontentbytes);
								
								dataoutputstream.writeInt(filenamebytes.length);
								dataoutputstream.write(filenamebytes);
								
								dataoutputstream.writeInt(filecontentbytes.length);
								dataoutputstream.write(filecontentbytes);

								JOptionPane.showMessageDialog(frame, "Connected To : "+socket.getInetAddress());
								area.append("Connected to : "+socket.getInetAddress().toString()+"\n");
								
								//JOptionPane.showMessageDialog(frame, "You unique secret key for accessing this data is: "+secretkey);
							}
							catch(Exception exp)
							{
								exp.printStackTrace();
							}
							
						}
					}
					
					public void encryptfile() throws Exception
					{
						String text = jfilechooser.getSelectedFile().getPath();
						String encrypted = "encrypted.txt";
						String decrypted = "decrypted.txt";
						byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
						IvParameterSpec ivspec = new IvParameterSpec(iv);
						SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
						KeySpec keyspec = new PBEKeySpec(secretkey.toCharArray(), saltvalue.getBytes(), 65536, 256);
						SecretKey skey = skf.generateSecret(keyspec);
						SecretKeySpec skeyspec = new SecretKeySpec(skey.getEncoded(), "AES");
						encrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
						encrypt.init(Cipher.ENCRYPT_MODE, skeyspec, ivspec);
						decrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
						decrypt.init(Cipher.DECRYPT_MODE, skeyspec, ivspec);
						encryption(new FileInputStream(text), new FileOutputStream(encrypted));
						//decryption(new FileInputStream(encrypted), new FileOutputStream(decrypted));
					}

					public void encryption(InputStream input, OutputStream output) throws Exception 
					{
				        output = new CipherOutputStream(output, encrypt);
				        writebytes(input, output);
				    }

				    public void decryption(InputStream input, OutputStream output) throws Exception 
				    {
				        input = new CipherInputStream(input, decrypt);
				        writebytes(input, output);
				    }
				    public void writebytes(InputStream input, OutputStream output) throws Exception 
				    {
				        byte[] writebuffer = new byte[512];
				        int readbytes = 0;
				        while ((readbytes = input.read(writebuffer)) >= 0) {
				            output.write(writebuffer, 0, readbytes);
				        }
				        output.close();
				        input.close();
				    }
					
				});
		
	}
	
}