package phramusca.com.jamuzkids;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


//https://www.codeproject.com/Articles/547636/Android-Ready-to-use-simple-directory-chooser-dial
//+fixes (".." button + title)
public class DirectoryChooserDialog
{
    private boolean m_isNewFolderEnabled = true;
    private String m_sdcardDirectory;
    private Context m_context;
    private TextView m_titleView;

    private String m_dir = "";
    private List<String> m_subdirs = null;
    private ChosenDirectoryListener m_chosenDirectoryListener;
    private ArrayAdapter<String> m_listAdapter = null;

    public interface ChosenDirectoryListener {
        void onChosenDir(String chosenDir);
    }

    DirectoryChooserDialog(Context context, ChosenDirectoryListener chosenDirectoryListener) {
        m_context = context;
        m_sdcardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        m_chosenDirectoryListener = chosenDirectoryListener;
        m_sdcardDirectory = new File(m_sdcardDirectory).getAbsolutePath();
    }

    public void setNewFolderEnabled(boolean isNewFolderEnabled) {
        m_isNewFolderEnabled = isNewFolderEnabled;
    }

    public void chooseDirectory(String dir) {
        File dirFile = new File(dir);
        if (! dirFile.exists() || ! dirFile.isDirectory()) {
            dir = m_sdcardDirectory;
        }
        dir = new File(dir).getAbsolutePath();
        m_dir = dir;
        m_subdirs = getDirectories(dir);

        class DirectoryOnClickListener implements DialogInterface.OnClickListener {
            public void onClick(DialogInterface dialog, int item) {
                String subFolder = (String) ((AlertDialog) dialog).getListView() .getAdapter().getItem(item);
                if (subFolder.equals("..")) {
                    int lastSlash = m_dir.lastIndexOf('/');
                    if (lastSlash>0 ){
                        m_dir = m_dir.substring(0, lastSlash);
                    } else {
                        m_dir = m_dir.substring(0, 1);
                    }
                } else if (!m_dir.equals("/")){
                    m_dir += "/" + subFolder;
                } else {
                    m_dir += subFolder;
                }
                updateDirectory();
            }
        }

        AlertDialog.Builder dialogBuilder =
                createDirectoryChooserDialog(dir, m_subdirs, new DirectoryOnClickListener());

        dialogBuilder.setPositiveButton(R.string.ok, (dialog, which) -> {
            if (m_chosenDirectoryListener != null) {
                m_chosenDirectoryListener.onChosenDir(m_dir);
            }
        }).setNegativeButton(R.string.cancel, null);

        final AlertDialog dirsDialog = dialogBuilder.create();

        dirsDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                // Back button pressed
                if ( m_dir.equals(m_sdcardDirectory) )
                {  // The very top level directory, do nothing
                    return false;
                }
                else
                {  // Navigate back to an upper directory
                    m_dir = new File(m_dir).getParent();
                    updateDirectory();
                }
                return true;
            }
            else
            {
                return false;
            }
        });
        dirsDialog.show();
    }

    private boolean createSubDir(String newDir) {
        File newDirFile = new File(newDir);
        return !newDirFile.exists() && newDirFile.mkdir();
    }

    private List<String> getDirectories(String dir) {
        List<String> dirs = new ArrayList<>();
        try {
            File dirFile = new File(dir);
            if (! dirFile.exists() || ! dirFile.isDirectory()) {
                return dirs;
            }
            dirs.add("..");
            for (File file : dirFile.listFiles()) {
                if ( file.isDirectory() ) {
                    dirs.add( file.getName() );
                }
            }
        }
        catch (Exception ignored) {
        }
        Collections.sort(dirs, String::compareTo);
        return dirs;
    }

    private AlertDialog.Builder createDirectoryChooserDialog(String title, List<String> listItems,
                                                             DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(m_context);
        LinearLayout titleLayout = new LinearLayout(m_context);
        titleLayout.setOrientation(LinearLayout.VERTICAL);

        m_titleView = new TextView(m_context);
        m_titleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        //m_titleView.setTextAppearance(m_context, android.R.style.TextAppearance_Large);
        //m_titleView.setTextColor( m_context.getResources().getColor(android.R.color.white) );
        m_titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        m_titleView.setText(title);

        Button newDirButton = new Button(m_context);
        newDirButton.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        newDirButton.setText(R.string.newFolder);
        newDirButton.setOnClickListener(v -> {
            final EditText input = new EditText(m_context);
            new AlertDialog.Builder(m_context).
                    setTitle(R.string.newFolderName).
                    setView(input).setPositiveButton(R.string.ok, (dialog, whichButton) -> {
                        Editable newDir = input.getText();
                        String newDirName = newDir.toString();
                        // Create new directory
                        if ( createSubDir(m_dir + "/" + newDirName) ) {
                            // Navigate into the new directory
                            m_dir += "/" + newDirName;
                            updateDirectory();
                        }
                        else {
                            Toast.makeText(
                                    m_context, "Failed to create '" + newDirName +
                                            "' folder", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton(R.string.cancel, null).show();
        });
        if (! m_isNewFolderEnabled) {
            newDirButton.setVisibility(View.GONE);
        }
        titleLayout.addView(m_titleView);
        titleLayout.addView(newDirButton);
        dialogBuilder.setCustomTitle(titleLayout);
        m_listAdapter = createListAdapter(listItems);
        dialogBuilder.setSingleChoiceItems(m_listAdapter, -1, onClickListener);
        dialogBuilder.setCancelable(false);

        return dialogBuilder;
    }

    private void updateDirectory() {
        m_subdirs.clear();
        m_subdirs.addAll( getDirectories(m_dir) );
        m_titleView.setText(m_dir);
        m_listAdapter.notifyDataSetChanged();
    }

    private ArrayAdapter<String> createListAdapter(List<String> items)
    {
        return new ArrayAdapter<String>(m_context,
                android.R.layout.select_dialog_item, android.R.id.text1, items) {
            @NonNull
            @Override
            public View getView(int position, View convertView,
                                @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    tv.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                    tv.setEllipsize(null);
                }
                return v;
            }
        };
    }
}
