import java.io.File;

import javax.swing.JFileChooser;

public class FileChooser {
	private static FileChooser instance;

	private FileChooser() {

	}
	
	
	public static FileChooser getInstance() {
		if(instance == null) {
		 instance = new FileChooser();
		}
		return	instance;
	}

	/*escolhe o caminho do arquivo*/
	public String choosePath() {
		/*instancia a variavel de escolher o arquivo*/
		JFileChooser fileChooser = new JFileChooser();
		
		/*permite que o usuário selecione apenas diretórios (pastas) em vez de arquivos.*/
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		/* utilizada para exibir o diálogo de salvar arquivo  para que o usuário possa escolher onde salvar um arquivo selecionado ou digitar um novo nome de arquivo.*/
		int result = fileChooser.showSaveDialog(null);
		
		/*Quando o usuario salva o arquivo*/
		if(result == JFileChooser.APPROVE_OPTION) {
			/* pega o arquvio selecionado*/
			File selectedFile = fileChooser.getSelectedFile();
			
			/*pega o caminho absoluto do arquivo*/
			return selectedFile.getAbsolutePath();
		}
		
		return "";
	}
}
