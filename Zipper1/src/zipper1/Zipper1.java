/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zipper1;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.*;

/**
 *
 * @author kamil
 */
public class Zipper1 extends JFrame{
 private Zipper1(){
        this.setTitle("Zipper");
        this.setBounds(275, 300, 250, 250);
        this.setJMenuBar(pasekMenu);
        
        JMenu menuPlik = pasekMenu.add(new JMenu("Plik"));
             
        Action akcjaDodawania = new Akcja("Dodaj", "Dodaj nowy wpis do archiwum", "ctrl D", new ImageIcon("dodaj.png"));
        Action akcjaUsuwania = new Akcja("Usuń", "Usuń zaznaczony/zaznaczone wpisy z archiwum", "ctrl U", new ImageIcon("usun.png"));
        Action akcjaZipowania = new Akcja("Zip", "Zipuj", "ctrl Z");
        
        JMenuItem menuOtworz = menuPlik.add(akcjaDodawania);
        JMenuItem menuUsun = menuPlik.add(akcjaUsuwania);
        JMenuItem menuZip = menuPlik.add(akcjaZipowania);
        
        bDodaj = new JButton(akcjaDodawania);
        bUsun = new JButton(akcjaUsuwania);
        bZip = new JButton(akcjaZipowania);
      
        JScrollPane sc=new JScrollPane(lista);
        lista.setBorder(BorderFactory.createEtchedBorder());
        GroupLayout layout = new GroupLayout(this.getContentPane());
        
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                .addComponent(sc, 100, 150, Short.MAX_VALUE)
                .addContainerGap(0, Short.MAX_VALUE)
                .addGroup(
                layout.createParallelGroup().addComponent(bDodaj).addComponent(bUsun).addComponent(bZip)
                
                )
                );
        
        layout.setVerticalGroup(
                layout.createParallelGroup()
                .addComponent(sc, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup().addComponent(bDodaj).addComponent(bUsun).addGap(5, 40, Short.MAX_VALUE).addComponent(bZip))
                );
        
        this.getContentPane().setLayout(layout);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
       // this.pack();
    }
    private JFileChooser chooser=new JFileChooser(System.getProperty("user.dir"));
    private AdjustModel model=new AdjustModel();
    private JList lista = new JList(model);
    private JButton bDodaj;
    private JButton bUsun;
    private JButton bZip;
    private JMenuBar pasekMenu = new JMenuBar();
    private int countOfZips=0;
    public static void main(String[] args)
    {
        new Zipper1().setVisible(true);
    }
    
    
    private class Akcja extends AbstractAction
    {
        public Akcja(String nazwa, String opis, String klawiaturowySkrot)
        {
            this.putValue(Action.NAME, nazwa);
            this.putValue(Action.SHORT_DESCRIPTION, opis);
            this.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(klawiaturowySkrot));
        }
        public Akcja(String nazwa, String opis, String klawiaturowySkrot, Icon ikona)
        {
            this(nazwa, opis ,klawiaturowySkrot);
            this.putValue(Action.SMALL_ICON, ikona);
        }

        public void actionPerformed(ActionEvent e) 
        {
           if (e.getActionCommand().equals("Dodaj")){
         
           chooser.setMultiSelectionEnabled(true);
           chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
           int tmp =chooser.showDialog(rootPane,"Dodaj");
           if(tmp==JFileChooser.APPROVE_OPTION){
           File [] files =chooser.getSelectedFiles();
           for(File f:files){dodajDoListy(f);}
           }
           }
           else if (e.getActionCommand().equals("Usuń"))
           {chooser.setMultiSelectionEnabled(false);
            int tmp=chooser.showDialog(rootPane,"Usuń");
            if(tmp==JFileChooser.APPROVE_OPTION){
            File file=chooser.getSelectedFile();
            model.removeObject(file);
            }
           }
           else if (e.getActionCommand().equals("Zip")){
           doZip();
           
           }
               
        }
        
        
        private void dodajDoListy(File f){
        if(!sprawdzCzySiePowtarze(f)){model.addElement(f);}
        }
        
        private boolean sprawdzCzySiePowtarze(File f){
        for(int i=0;i<model.getSize();i++){
        if(((File)model.get(i)).equals(f)){return true;}
        }
            return false;
        }
        
        
       private void doZip(){
      
       
        try {
            ZipOutputStream zipS=new ZipOutputStream(new BufferedOutputStream(new FileOutputStream("NowyZip"+countOfZips+".zip"),1024));
            ArrayList list=model.getArrayList();
            for(int i =0;i<list.size();i++){
           
                if(!((File)list.get(i)).isDirectory()){
                zip(zipS,(File)list.get(i));
                }
                else {
                    wypiszSciezki((File)list.get(i));
                    for(int a=0;a<listaSciezek.size();a++){
                    zip(zipS,(File)listaSciezek.get(a));
                    }
                }
            }
            zipS.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Zipper1.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException E){
            System.out.println(E.getMessage());
        }
       }
       private void zip(ZipOutputStream zipS,File sciezka)throws IOException{ 
           final int buffer=1024;
           byte []tmp=new byte[buffer];
           BufferedInputStream inS=new BufferedInputStream(new FileInputStream(sciezka));
            int counter;
            zipS.putNextEntry(new ZipEntry(sciezka.getName()));
            while((counter=inS.read(tmp,0,buffer))!=-1){
            zipS.write(tmp, 0, counter);
            }
            inS.close();
            zipS.closeEntry();
            listaSciezek.removeAll(listaSciezek);
            }
      private void wypiszSciezki(File nazwaSciezki)
        {
           String[] nazwyPlikowIKatalogow = nazwaSciezki.list();

           for (int i = 0; i < nazwyPlikowIKatalogow.length; i++)
           {
               File p = new File(nazwaSciezki.getPath(), nazwyPlikowIKatalogow[i]);

               if (p.isFile())
                   listaSciezek.add(p);

               if (p.isDirectory())
                   wypiszSciezki(new File(p.getPath()));
           }
           
        }
       ArrayList listaSciezek = new ArrayList();
    }
    
    /**
     *
     */
    public class AdjustModel extends DefaultListModel{
    
    public AdjustModel(){
    super();
    }
    
    @Override
    public void addElement(Object e){
    super.addElement(((File)e).getName());
    list.add(e);
    }
    
    @Override
    public Object get(int index){
    return list.get(index);
    }  
   
    public void removeObject(Object obj){
    for(int i=0;i<list.size();i++){
    if(list.get(i).equals(obj)){
    super.removeElementAt(i);
    list.remove(i);break;
    }
    }
    }
    
    public ArrayList getArrayList(){
    return list;
    }
    
    private final ArrayList list=new ArrayList();
   
    }
    
    
    
    
    
}
