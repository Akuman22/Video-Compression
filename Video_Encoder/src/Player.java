import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Player extends JFrame implements ActionListener, ChangeListener{
	JFrame f = new JFrame("EC504 Group - 1 Video Player");
	int index = 0;
	JLabel picLabel;
	int seq = 0;
	JPanel window;
	Thread render;
	JButton closeButton;
	JButton playButton;
	JComboBox<String> cb;
	
	int effectId = 0;
	boolean playing = false;
	public BufferedImage frame = null;
	EffectsApply myEffects;
	
	Player() throws IOException{
		closeButton = new JButton();
		playButton = new JButton();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        myEffects = new EffectsApply();
        
        String[] choices = { "No Effect","Sepia", "Sharpen","Heat Map","Face HDR","Blur", "Film Grain"};
        cb = new JComboBox<String>(choices);
        cb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            		System.out.println(cb.getSelectedIndex());
            		effectId = cb.getSelectedIndex();
            }
        });
        cb.setBounds((int)(width*1600/1920), (int)(height*850/1080),(int)(width*400/1920), (int)(height*40/1080) );
        cb.setFont(new Font("Lucida Sans Italic", Font.BOLD, (int)(20.0*((width)/(1920.0)))));

		closeButton.setBounds((int)width * 1850/1920, (int)height*0/1080,60, 50 );
        closeButton.setBounds((int)(width*1850/1920), 0,(int)(width*60/1920), (int)(height*50/1080) );
        closeButton.setFont(new Font("Arial", Font.BOLD, (int)(30*(1920*1080)/(width*height))));
        closeButton.setText("X");
        closeButton.setForeground(Color.red);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(this);
        
        playButton.setBounds((int)(width*1600/1920), (int)(height*900/1080),(int)(width*250/1920), (int)(height*50/1080) );
        playButton.setFont(new Font("Lucida Sans Italic", Font.BOLD, 10));
        playButton.setText("PLAY/PAUSE");
        playButton.setForeground(Color.green);
        playButton.setBorderPainted(true);
        playButton.setContentAreaFilled(false);
        playButton.setFocusPainted(false);
        playButton.setOpaque(false);
        playButton.addActionListener(this);
        
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setForeground(Color.red);
            }
        });
        
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playButton.setForeground(Color.white);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                playButton.setForeground(Color.green);
            }
        });

        f.add(closeButton);
        f.add(playButton);
        f.add(cb);
        
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());
        
        window = new JPanel();
        window.setBackground(Color.BLACK);
        window.setLayout(new GridBagLayout());

        BufferedImage frame = ImageIO.read( new File("back.jpg"));
        picLabel = new JLabel(new ImageIcon(frame));

        window.add(picLabel);
        
        window.setSize((int)width, (int)height);
        f.add(window);

        //f.setContentPane(new JLabel(new ImageIcon("player.jpg")));
        f.setLayout(new FlowLayout());
        f.setLayout(null);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setUndecorated(true);
		
		f.setVisible(true);
		play();

		
	}
	
	public void play() throws IOException {
		render = new Thread(new Runnable() { // main updating loop
		      public void run() {
		        // do the animation repeatedly

		    	  Thread thisThread = Thread.currentThread();
		    	  while(render == thisThread) {
		    		  	if (playing) {
			  			/*String fName = "75/" + Integer.toString(i) + ".jpg";
						try {
							frame = ImageIO.read( new File(fName));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						*/
						frame = BuildFrame();
						frame = myEffects.apply(effectId, frame);
						
						
			  			 picLabel.setIcon(new ImageIcon(frame));
			  			 cb.repaint();
			  			 playButton.repaint();
			  			 closeButton.repaint();
		    		  	}
		  			 try {
		  				Thread.sleep(30);
		  			} catch (InterruptedException e) {
		  				// TODO Auto-generated catch block
		  				e.printStackTrace();
		  			}
		  		}
		      }
		      
		      
		    });
		render.start();
		
		
		
		
		
		
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd = e.getActionCommand();
		if (cmd == "X") {
			render = null;
			f.dispose();
		}
		if (cmd == "PLAY/PAUSE") {
			playing = !playing;
		}
	}
	
	public BufferedImage BuildFrame() {
		int theWidth = Main.ob1.imageWidth; int theHeight = Main.ob1.imageHeight;
		BufferedImage newImg = new BufferedImage(theWidth, theHeight, BufferedImage.TYPE_INT_RGB);

		for (int row = 0; row < theHeight; row +=8) {
			for (int col = 0; col < theWidth; col += 8) {
				try {
					newImg.createGraphics().drawImage(Main.ob1.macroblocks.get(index), col, row, 9, 9, null);
					index++;
				}
				catch (Exception e){
					index = 0;
					break;

				}
			}
		}
		if(index == Main.ob1.macroblocks.size())
		{
			index = 0;
		}
		return newImg;
	}
	
	
	
	
	
	
}
