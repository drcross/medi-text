package ie.dylancross.drcross.myapplication4;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.content.res.AssetManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.provider.UserDictionary;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.TextUtils;
import android.webkit.WebView;

public class MainActivity extends Activity
{
    private Button btnLoadDictionary;
    private Button btnUnLoadDictionary;
    private Button about_button;
    private ProgressBar pbLoadDictionary;
    private TextView txtUpdateStatus;
    // For progressbar update ui.
    private Handler hHandler = new Handler();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadDictionary = (Button) findViewById(R.id.loadDictionary);
        btnUnLoadDictionary = (Button) findViewById(R.id.UnloadDictionary);
        about_button = (Button) findViewById(R.id.about_button);
        pbLoadDictionary = (ProgressBar) findViewById(R.id.pbLoadDictionary);
        pbLoadDictionary.setIndeterminate(false);
        txtUpdateStatus = (TextView) findViewById(R.id.txtUpdateStatus);

        about_button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://dylandylandylan.com/medi-text.html"));
                startActivity(browserIntent);
            }
        });

        btnLoadDictionary.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // To avoid more than one click.
                btnLoadDictionary.setEnabled(false);
                btnUnLoadDictionary.setEnabled(false);
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        loadDictionary();
                    }
                }).start();
            }
        });
        btnUnLoadDictionary.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // To avoid more than one click.
                btnUnLoadDictionary.setEnabled(false);
                btnLoadDictionary.setEnabled(false);
                new Thread(new Runnable()
                {
                    public void run()
                    {
                        unloadDictionary();
                    }
                }).start();
            }
        });
    }

    protected void loadDictionary()
    {
        BufferedReader reader = null;
        int numberOfLines = 0;
        try
        {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("CustomDirectory.txt"), "UTF-8"));
            String text = null;
            // So we can go back when finish reading the number of lines.
            // repeat until all lines is read
            while ((text = reader.readLine()) != null)
            {
                numberOfLines++;
            }
            pbLoadDictionary.setMax(numberOfLines);
            pbLoadDictionary.setProgress(0);
            reader.close();
            reader = new BufferedReader(new InputStreamReader(getAssets().open("CustomDirectory.txt"), "UTF-8"));
            // repeat until all lines is read
            while ((text = reader.readLine()) != null)
            {
                UserDictionary.Words.addWord(this, text, 240, UserDictionary.Words.LOCALE_TYPE_ALL);
                hHandler.post(new Runnable()
                {
                    public void run()
                    {
                        pbLoadDictionary.incrementProgressBy(1);
                        //pbLoadDictionary.incrementSecondaryProgressBy(1);
                        txtUpdateStatus.setText(String.format("Loading words %s  / %s", pbLoadDictionary.getProgress(), pbLoadDictionary.getMax()));
                    }
                });

            }

            hHandler.post(new Runnable()
            {
                public void run()
                {
                    btnLoadDictionary.setEnabled(false);
                    btnUnLoadDictionary.setEnabled(true);
                    pbLoadDictionary.setProgress(0);
                    txtUpdateStatus.setText(String.format("Finished loading %s words to dictionary", pbLoadDictionary.getMax()));
                }
            });


        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    protected void unloadDictionary()
    {
        BufferedReader reader = null;
        int numberOfLines = 0;
        try
        {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("CustomDirectory.txt"), "UTF-8"));
            String text = null;
            // So we can go back when finish reading the number of lines.
            // repeat until all lines is read
            while ((text = reader.readLine()) != null)
            {
                numberOfLines++;
            }
            pbLoadDictionary.setMax(numberOfLines);
            pbLoadDictionary.setProgress(0);
            reader.close();
            reader = new BufferedReader(new InputStreamReader(getAssets().open("CustomDirectory.txt"), "UTF-8"));
            // repeat until all lines is read
            while ((text = reader.readLine()) != null)
            {
                getContentResolver().delete(UserDictionary.Words.CONTENT_URI, UserDictionary.Words.WORD + "=?", new String[] { text });
                hHandler.post(new Runnable() {
                    public void run() {
                        pbLoadDictionary.incrementProgressBy(1);
                        //pbLoadDictionary.incrementSecondaryProgressBy(1);
                        txtUpdateStatus.setText(String.format("Removing words %s  / %s", pbLoadDictionary.getProgress(), pbLoadDictionary.getMax()));
                    }
                });
            }

            hHandler.post(new Runnable()
            {
                public void run()
                {
                    btnLoadDictionary.setEnabled(true);
                    btnUnLoadDictionary.setEnabled(false);
                    pbLoadDictionary.setProgress(0);
                    txtUpdateStatus.setText(String.format("Finished removing %s words from dictionary", pbLoadDictionary.getMax()));
                }
            });
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}