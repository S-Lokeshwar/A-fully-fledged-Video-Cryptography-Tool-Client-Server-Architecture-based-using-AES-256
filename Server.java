import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.awt.Color;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server 
{
	static ArrayList<MyFile> myfiles = new ArrayList<>();
	private static JPanel panel;
	private static JFrame frame;
	private static JTextArea area;
    private static KeyGenerator kgen ;
	private static SecretKey skey ;
	public static void main(String[] args) throws IOException 
	{
		int fileid = 0;
		
		frame = new JFrame("Server");
		frame.setSize(650, 700);
		frame.setLayout(new BoxLayout(frame.getContentPane(),BoxLayout.Y_AXIS));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.getContentPane().setBackground(Color.BLACK);
		//.getContentPane().setBackground(Color.RED);
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.BLACK);
		
		JScrollPane scroll = new JScrollPane(panel);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JLabel title = new JLabel("SECURE SERVER :)");
		title.setFont(new Font("Times New Roman",Font.BOLD,30));
		title.setBorder(new EmptyBorder(20,0,10,0));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setForeground(Color.WHITE);
		
		JPanel p1=new JPanel();
		p1.setLayout(new BoxLayout(p1,BoxLayout.Y_AXIS));
		p1.setAlignmentX(Component.CENTER_ALIGNMENT);
		p1.setBackground(Color.BLACK);

		JLabel op = new JLabel("Choose Operation : ");
		op.setFont(new Font("Calibri",Font.BOLD,20));
		op.setBorder(new EmptyBorder(20,0,10,0));
		op.setAlignmentX(Component.CENTER_ALIGNMENT);
		op.setForeground(Color.WHITE);

		JLabel dm = new JLabel("  ");
		dm.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton jre=new JButton("Encryption");
		jre.setPreferredSize(new Dimension(150,75));
		jre.setFont(new Font("Times New Roman", Font.BOLD, 20));
		jre.setAlignmentX(Component.CENTER_ALIGNMENT);
		jre.setFocusable(false);
		jre.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) 
					{
						// TODO Auto-generated method stub
						skey = encryptfile();
						
					}
					
				});
		//jre.setBounds(100, 200, 200, 200);
		JButton jrd=new JButton("Decryption");
		jrd.setPreferredSize(new Dimension(150,75));
		jrd.setAlignmentX(Component.CENTER_ALIGNMENT);
		jrd.setFont(new Font("Times New Roman", Font.BOLD, 20));
		//jrd.setBounds(200, 200, 200, 200);
		jrd.setFocusable(false);
		jrd.addActionListener(new ActionListener()
				{

					@Override
					public void actionPerformed(ActionEvent e) 
					{
						String sky = JOptionPane.showInputDialog(panel,"Enter the SecretKey : ");
						byte[] decodedKey = Base64.getDecoder().decode(sky);
  						SecretKey skey = new SecretKeySpec(decodedKey, 0,decodedKey.length, "AES");
						System.out.println(skey);
						Decryption(skey);
					}
					
				});
		
		JLabel i1 = new JLabel("Server's IP Address : 192.168.1.7");
		i1.setFont(new Font("Times New Roman",Font.BOLD,25));
		i1.setBorder(new EmptyBorder(20,0,10,0));
		i1.setAlignmentX(Component.CENTER_ALIGNMENT);
		i1.setForeground(Color.WHITE);

		JLabel rf = new JLabel("Received Files : ");
		rf.setFont(new Font("Calibri",Font.BOLD,20));
		rf.setBorder(new EmptyBorder(20,0,10,0));
		rf.setAlignmentX(Component.CENTER_ALIGNMENT);
		rf.setForeground(Color.WHITE);

		JLabel dev = new JLabel("Developed by:  Lokeshwar. S");
        dev.setFont(new Font("Times New Roman",Font.BOLD,25));
		dev.setBorder(new EmptyBorder(20,0,10,0));
        dev.setAlignmentX(Component.CENTER_ALIGNMENT);
		dev.setForeground(Color.WHITE);
		
		JButton cl=new JButton("Close");
		cl.setFont(new Font("Times New Roman",Font.BOLD,20));
		cl.setFocusable(false);
		cl.setAlignmentX(Component.CENTER_ALIGNMENT);
		cl.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				frame.dispose();			
			}
		});

		JLabel cnt = new JLabel("Connection History : ");
		cnt.setFont(new Font("Times New Roman",Font.BOLD,25));
		cnt.setBorder(new EmptyBorder(20,0,10,0));
		cnt.setAlignmentX(Component.CENTER_ALIGNMENT);
		cnt.setForeground(Color.WHITE);

		area = new JTextArea();
		//area.setBounds(10,30, 200,200); 
		area.setFont(new Font("Calibri",Font.BOLD,20));
		area.setEditable(false);
		
		frame.add(title);
		panel.add(p1);
		p1.add(i1);
		p1.add(op);
		p1.add(jre);
		p1.add(dm);
		p1.add(jrd);
		p1.add(rf);
		frame.add(scroll);
		panel.add(cnt);
		panel.add(area);
		frame.add(dev);
		frame.add(cl);

        
		
		frame.setVisible(true);
		
		ServerSocket serversocket = new ServerSocket(1500);
		
		while(true)
		{
			try
			{
				Socket socket = serversocket.accept();
				
				DataInputStream datainputstream = new DataInputStream(socket.getInputStream());
				
				int filenamelength = datainputstream.readInt();
				
				if(filenamelength > 0)
				{
					byte[] filenamebytes = new byte[filenamelength];
					datainputstream.readFully(filenamebytes, 0, filenamebytes.length);
					String filename = new String(filenamebytes);
					
					int filecontentlength = datainputstream.readInt();
					
					if(filecontentlength > 0)
					{
						byte[] filecontentbytes = new byte[filecontentlength];
						datainputstream.readFully(filecontentbytes, 0, filecontentlength);
						
						JPanel panelfilerow = new JPanel();
						panelfilerow.setBackground(Color.black);
						panelfilerow.setLayout(new BoxLayout(panelfilerow, BoxLayout.Y_AXIS));
						
						JLabel filenamelabel = new JLabel(filename);
						filenamelabel.setForeground(Color.white);
						filenamelabel.setFont(new Font("Arial", Font.BOLD, 20));
						filenamelabel.setBorder(new EmptyBorder(10,0,10,0));
						
						if(getFileExtension(filename).equalsIgnoreCase("avi"))
						{
							panelfilerow.setName(String.valueOf(fileid));
							panelfilerow.addMouseListener(getMyMouseListener());
							
							panelfilerow.add(filenamelabel);
							panelfilerow.setAlignmentX(Component.CENTER_ALIGNMENT);
							p1.add(panelfilerow);
							frame.validate();
							
						}
						else
						{
							panelfilerow.setName(String.valueOf(fileid));
							panelfilerow.addMouseListener(getMyMouseListener());
							panelfilerow.setAlignmentX(Component.CENTER_ALIGNMENT);
							panelfilerow.add(filenamelabel);
							p1.add(panelfilerow);
							frame.validate();
						}
						JOptionPane.showMessageDialog(frame, "Connection from : "+socket.getInetAddress());

						area.append("Connection from : "+socket.getInetAddress().toString()+"\n");
						
						
						frame.setVisible(true);
						frame.validate();
						
						
						
						myfiles.add(new MyFile(fileid, filename, filecontentbytes, getFileExtension(filename)));
					}
					
					
				}
				else
				{
					JOptionPane.showMessageDialog(frame, "No contents inside the file");
				}
			}
			catch(Exception exp)
			{
				exp.printStackTrace();
			}	
		}
	}
	
	private  static void Decryption(SecretKey sk)
	{
		try
		{	
            // File temp = new File("");
            // OutputStream os = new FileOutputStream(temp);
            // os.write(myfiles.get(myfiles.size()-1).getdata());
            // os.close();
            FileInputStream encfis = new FileInputStream("Encrypted-Video.mkv");
			File decfile = new File("Decrypted-Video.mp4");
            int read;
            if(!decfile.exists())
                decfile.createNewFile();
            FileOutputStream decfos = new FileOutputStream(decfile);
            Cipher decipher = Cipher.getInstance("AES");
            kgen = KeyGenerator.getInstance("AES");
            decipher.init(Cipher.DECRYPT_MODE, sk);
            CipherOutputStream cos = new CipherOutputStream(decfos,decipher);
            while((read=encfis.read())!=-1)
            {
                cos.write(read);
                cos.flush();
            }
            cos.close();
			JOptionPane.showMessageDialog(frame, "Decryption Done Succesfully !");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static SecretKey encryptfile() 
	{
		try
		{
			FileInputStream fis = new FileInputStream(new File("Confidential.mp4"));
            File outfile = new File("Encrypted-Video.mkv");
            int read;
            File file11 = new File("SK.txt");
            if(!outfile.exists())
                outfile.createNewFile();
            FileOutputStream fos = new FileOutputStream(outfile);
            FileInputStream encfis = new FileInputStream(outfile);
            Cipher encipher = Cipher.getInstance("AES");
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecretKey skey = kgen.generateKey();
			System.out.println(skey);
			byte[] rawData = skey.getEncoded();
    		String encodedKey = Base64.getEncoder().encodeToString(rawData);
			UIManager.put("OptionPane.messageFont", new Font("Century", Font.BOLD, 15));
			JOptionPane.showMessageDialog(panel,"Encoded-Key : "+encodedKey);
			FileWriter output = new FileWriter("SK.txt");
		    output.write(encodedKey);
		    output.close();
            encipher.init(Cipher.ENCRYPT_MODE, skey);
            CipherInputStream cis = new CipherInputStream(fis, encipher);
            while((read = cis.read())!=-1)
                {
                    fos.write((char)read);
                    fos.flush();
                }   
            fos.close();
            //cos.close();
			JOptionPane.showMessageDialog(panel, "Encryption done Successfully !");
            return skey;
		}
		catch(Exception e)
		{
			e.printStackTrace();
            return null;
		}
	}
	public static MouseListener getMyMouseListener()
	{
		return new MouseListener()
				{
					
					public void mouseClicked(MouseEvent e)
					{
						JPanel jpanel = (JPanel) e.getSource();
						int fileID = Integer.parseInt(jpanel.getName());
						for(MyFile myfile: myfiles)
						{
							if(myfile.getid() == fileID)
							{
								JFrame jfpreview = null;
								try {
									jfpreview = createFrame(myfile.getname(),myfile.getdata(),myfile.getfileextension());
								} catch (Exception e1) 
								{
									JOptionPane.showMessageDialog(frame, "Unsupported Video format!");
								}
								jfpreview.setVisible(true);
							}
						}
					}
					
					private  JFrame createFrame(String fileName, byte[] fileData, String fileExtension) throws IOException
					{
						File filedata = new File("Sample.avi");
						JFrame jFrame = new JFrame("File Downloader"); 
						jFrame.setSize(400,400);
						JPanel jPanel = new JPanel();
						jPanel.setLayout(new BoxLayout (jPanel, BoxLayout.Y_AXIS));
						JLabel jlTitle = new JLabel("File downloader prompt");
						jlTitle.setAlignmentX (Component.CENTER_ALIGNMENT);
						jlTitle.setFont(new Font("Arial",Font.BOLD,25));
						jlTitle.setBorder (new EmptyBorder(20,0,10,0));
						JLabel jlPrompt = new JLabel("Are you sure you want to download "+fileName+" ?");
						jlPrompt.setFont(new Font("Arial",Font.BOLD,20));
						jlPrompt.setBorder(new EmptyBorder (20,0,10,0));
						jlPrompt.setAlignmentX (Component.CENTER_ALIGNMENT);
						JButton jbYes = new JButton("Yes");
						jbYes.setPreferredSize (new Dimension(150,75));
						jbYes.setFont(new Font("Arial",Font.BOLD,20));
						JButton jbNo = new JButton ("No");
						jbNo.setPreferredSize (new Dimension(150,75));
						jbNo.setFont(new Font("Arial",Font.BOLD,20));
						JLabel jlFileContent = new JLabel();
						jlFileContent.setAlignmentX (Component.CENTER_ALIGNMENT);
						JPanel jpButtons = new JPanel();
						jpButtons.setBorder(new EmptyBorder(20,0,10,0));
						jpButtons.add(jbYes);
						jpButtons.add(jbNo);
						if (fileExtension.equalsIgnoreCase("txt")) 
						{ 
							jlFileContent.setText("<html>" + new String(fileData) + "</html");
						}
						else
						{
							jlFileContent.setIcon(new ImageIcon(ImageIO.read(filedata)));
						}
						jbYes.addActionListener(new ActionListener()
						{
							
							public void actionPerformed (ActionEvent e)
							{ 
								File fileToDownload = new File("Sample.avi");
								try
								{
									FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
									fileOutputStream.write(fileData);
									fileOutputStream.close();	
									jFrame.dispose();
								}
								catch (Exception error)
								{
									JOptionPane.showMessageDialog(jFrame, "Unsupported Video format!");
								}
							}
						});
						jbNo.addActionListener(new ActionListener()
						{
							@Override
							public void actionPerformed(ActionEvent e)
							{
								jFrame.dispose();
							}
						});
						jPanel.add(jlTitle);
						jPanel.add(jlPrompt);
						jPanel.add(jlFileContent);
						jPanel.add(jpButtons);
						jFrame.add(jPanel);
						return jFrame;
					}
					
					public void mousePressed(MouseEvent e)
					{
						
					}
					
					public void mouseReleased(MouseEvent e)
					{
						
					}
					
					public void mouseEntered(MouseEvent e)
					{
						
					}
					public void mouseExited(MouseEvent e)
					{
						
					}
				};
	}
	public static String getFileExtension(String filename)
	{
		int i = filename.lastIndexOf('.');
		if(i > 0)
		{
			return filename.substring(i+1); 
		}
		else
		{
			return "No extention found";
		}
		}
}