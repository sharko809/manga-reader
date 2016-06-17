package com.example.mangareader;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.mangareader.CustomListeners.FileDialogDepends;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;

public class FileDialog extends AlertDialog {
	Context context;
	File dir;
	String[] files;
	AlertDialog.Builder builder;
	AlertDialog dialog;
	FilenameFilter filenameFilter;
	// PluginProperties plugin_props;
	FileDialogDepends fileDialogDepends;
	Intent intent;

	// public static String FILENAME_FILTER = "(?i)^\\-.*?\\.html$";
	public static String FILENAME_FILTER = "";
	public static String CURRENT_PATH = Environment.getExternalStorageDirectory().getPath();

	public static String ROOT_PATH = Environment.getRootDirectory().getPath();

	public FileDialog(Context context) {
		super(context);
		this.context = context;

		filenameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File directory, String fileName) {
				return fileName.matches(FILENAME_FILTER);
			}
		};
	}

	/**
	 * Вызов диалога открытия файла.
	 * 
	 * @param fileDialogDepends
	 *            – объект, ссылка на который передается из вызывающей activity
	 *            для взаимосвязи с ней.
	 */
	public void openFileDialog(FileDialogDepends fileDialogDepends) {
		this.fileDialogDepends = fileDialogDepends;
		openFileDialog();
	}

	/**
	 * Внутренняя процедура для рекурсивного переоткрытия диалога файла.
	 */
	private void openFileDialog() {
		builder = new AlertDialog.Builder(context);

		dir = new File(CURRENT_PATH);
		builder.setTitle(R.string.select_file);
		files = concatAll(dirs(dir), files(dir, filenameFilter));
		builder.setItems(files, listenerFileDialog);
		builder.setNegativeButton(android.R.string.cancel, null);

		dialog = builder.create();
		dialog.show();
	}

	/**
	 * Определяется реакция на нажатие кнопки на диалоге: – если выбран <..> –
	 * переходим на уровень вверх и вызываем openFileDialog – если выбрана папка
	 * – переходим в папку и вызываем openFileDialog – если выбран файл –
	 * сохраняем текущий путь в настройки, вызываем fileSelected, – если нажата
	 * отмена – закрываем диалог
	 */
	private DialogInterface.OnClickListener listenerFileDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();

			if (files[which].equals("..")) {// НАЖАТО НА <..>
				dir = new File(dir.getParent());
			} else
				dir = new File(CURRENT_PATH + "/" + files[which]);
			if (dir.isFile()) { // ВЫБРАН ФАЙЛ
				Editor ed = context.getSharedPreferences("preferences", Context.MODE_PRIVATE).edit();
				ed.putString("pref_current_path", CURRENT_PATH);
				ed.apply();
				fileSelected(dir);
			} else if (dir.isDirectory()) { // ВЫБРАНА ПАПКА
				if (!files[which].equals(".."))
					dir = new File(CURRENT_PATH + "/" + files[which]);
				CURRENT_PATH = dir.toString();
				openFileDialog(fileDialogDepends);
			}
		}
	};

	/**
	 * Файл выбран.
	 * 
	 * @param file
	 *            – выбранный файл.
	 */
	public void fileSelected(File file) {

		// ЗДЕСЬ ПИШЕМ ПРОЦЕДУРЫ, КОТОРЫЕ РЕАЛИЗУЮТСЯ, КОГДА
		// ФАЙЛ ВЫБРАН. ТО ЕСТЬ ОТКРЫТИЕ ФАЙЛА, ОБРАБОТКА,
		// КОПИРОВАНИЕ И ТАК ДАЛЕЕ.
		intent = new Intent(context, PictureActivity.class);
		intent.putExtra("path", file.getAbsolutePath());

		File[] listFile = file.getParentFile().listFiles(fileNameFilter());
		String[] listFiles = new String[listFile.length];
		for (int i = 0; i < listFiles.length; i++) {
			listFiles[i] = listFile[i].toString();
		}
		Arrays.sort(listFiles);
		intent.putExtra("fileList", listFiles);
		context.startActivity(intent);
		// Здесь проводим взаимосвязь с вызывающей activity.
		// fileDialogDepends.refresh();

	}

	/**
	 * Выдает отсортированный список папок из папки path. 1) Игнорируются папки,
	 * начинающиеся с точки. 2) В начало списка помещается пункт "..". 3)
	 * Сортировка осуществляется в предпоследней строке, для
	 * регистронезависимого варианта необходимо определить соответствующий
	 * компаратор.
	 */
	public static String[] dirs(File path) {
		final List<String> files = new ArrayList<String>();

		if (!path.toString().equals(ROOT_PATH))
			files.add("..");

		for (File a : path.listFiles()) {
			if (a.isDirectory() && !a.getName().toString().startsWith("."))
				files.add(a.getName().toString() + "/");
		}
		String[] res = (String[]) files.toArray(new String[files.size()]);
		// Arrays.sort(res, new SortedByName());
		Arrays.sort(res);
		return res;
	}

	/**
	 * Выдает отсортированный список файлов из папки path, отфильтрованный в
	 * соответствии с фильтром filter. Сортировка осуществляется в предпоследней
	 * строке, для регистронезависимого варианта необходимо определить
	 * соответствующий компаратор.
	 */
	public static String[] files(File path, FilenameFilter filter) {
		final List<String> files = new ArrayList<String>();

		for (File a : path.listFiles(fileNameFilter()))
			if (a.isFile())
				files.add(a.getName().toString());

		String[] res = (String[]) files.toArray(new String[files.size()]);
		// Arrays.sort(res, new SortedByName());
		Arrays.sort(res);
		return res;
	}

	/**
	 * Объединение массивов.
	 * 
	 * @param first
	 * @param rest
	 * @return
	 */
	public static <T> T[] concatAll(T[] first, T[]... rest) {
		int totalLength = 0;
		if (first != null)
			totalLength = first.length;
		for (T[] array : rest)
			if (array != null)
				totalLength += array.length;
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			if (array != null) {
				System.arraycopy(array, 0, result, offset, array.length);
				offset += array.length;
			}
		}
		return result;
	}
	
	public static FilenameFilter fileNameFilter () {
		FilenameFilter fileNameFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf('.') > 0) {
					// get last index for '.' char
					int lastIndex = name.lastIndexOf('.');

					// get extension
					String str = name.substring(lastIndex);

					// match path name extension
					if (str.equals(".jpg") || str.equals(".png") || str.equals(".PNG") || str.equals(".JPEG")
							|| str.equals(".jpeg") || str.equals(".zip") || str.equals(".rar")) {
						return true;
					}
				}
				return false;
			}
		};
		return fileNameFilter;
	}

}
