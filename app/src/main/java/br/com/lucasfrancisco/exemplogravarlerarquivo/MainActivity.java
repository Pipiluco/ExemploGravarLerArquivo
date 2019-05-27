package br.com.lucasfrancisco.exemplogravarlerarquivo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView tvCaminhoArquivos, btnExplorerArquivos, btnExplorerPastas;
    private Button btnExplorerImagens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCaminhoArquivos = (TextView) findViewById(R.id.tvCaminhoArquivos);
        btnExplorerImagens = (Button) findViewById(R.id.btnExplorerImagens);
        btnExplorerArquivos = (Button) findViewById(R.id.btnExplorerArquivos);
        btnExplorerPastas = (Button) findViewById(R.id.btnExplorerPastas);

        getBtnExplorerImagens();
        getBtnExplorerPastas();
        getBtnExplorerArquivos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    requestLoadImage(data);
                    break;
                case 2:
                    requestEditFile(data);
                    break;
            }
        }
    }

    public void getBtnExplorerImagens() {
        btnExplorerImagens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Arquivos"), 1);
            }
        });
    }

    public void getBtnExplorerArquivos() {
        btnExplorerArquivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria arquivo
                // Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                //  intent.addCategory(Intent.CATEGORY_OPENABLE);
                //  intent.setType("*/*");
                //  intent.putExtra(Intent.EXTRA_TITLE, "Teste.txt");
                //  startActivityForResult(intent, 1);


                // Edita arquivo
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(intent, 1);
            }
        });
    }

    public void getBtnExplorerPastas() {
        btnExplorerPastas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    //////////////////////////////////////////
    public void requestLoadImage(Intent intent) {
        if (intent.getClipData() != null) {
            int totalArquivosSelecionado = intent.getClipData().getItemCount();

            for (int i = 0; i < totalArquivosSelecionado; i++) {
                Uri uri = intent.getClipData().getItemAt(i).getUri();
                String nomeArquivo = uri + getNomeArquivo(uri);

                tvCaminhoArquivos.append(nomeArquivo + "\n");
            }
        } else if (intent.getData() != null) {
            Uri uri = intent.getData();
            String nomeArquivo = uri + getNomeArquivo(uri);

            tvCaminhoArquivos.append(nomeArquivo + "\n");
        }
    }


    public void requestEditFile(Intent intent) {
        if (intent.getData() != null) {
            Uri uri = intent.getData();
            String nomeArquivo = uri + getNomeArquivo(uri);

            try {
                ParcelFileDescriptor parcelFileDescriptor = getApplicationContext().getContentResolver().openFileDescriptor(uri, "w");
                FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                fileOutputStream.write(("Overwritten by MyCloud at " + System.currentTimeMillis() + "\n").getBytes());
                fileOutputStream.close();
                parcelFileDescriptor.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tvCaminhoArquivos.append(nomeArquivo + "\n");
        }
    }

    // Retorna o nome de um arquivo selecionado no gerenciador de arquivos
    public String getNomeArquivo(Uri uri) {
        String resultado = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    resultado = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        if (resultado == null) {
            resultado = uri.getPath();
            int corte = resultado.lastIndexOf('/');
            if (corte != -1) {
                resultado = resultado.substring(corte + 1);
            }
        }
        return resultado;
    }
}
