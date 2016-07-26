package com.example.johnny.android_introduccion_hilos;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by Johnny on 21/06/2016.
 */

/*
onPreExecute(). Se ejecutará antes del código principal de nuestra tarea. Se suele utilizar para
                preparar la ejecución de la tarea, inicializar la interfaz, etc.
doInBackground(). Contendrá el código principal de nuestra tarea.
onProgressUpdate(). Se ejecutará cada vez que llamemos al método publishProgress() desde el método
                    doInBackground().
onPostExecute(). Se ejecutará cuando finalice nuestra tarea, o dicho de otra forma, tras la
                 finalización del método doInBackground().
onCancelled(). Se ejecutará cuando se cancele la ejecución de la tarea antes de su finalización
               normal.
*/


/*Los parametros que estamos recibiendo al heredar del AsyncTask son:

      Parametro 1: El tipo de datos que recibiremos como entrada de la tarea en el método
                   doInBackground(). En este caso doInBackground() no recibirá ningún parámetro
                   de entrada (Void).
      Parametro 2: El tipo de datos con el que actualizaremos el progreso de la tarea, y que
                   recibiremos como parámetro del método onProgressUpdate() y que a su vez
                   tendremos que incluir como parámetro del método publishProgress(). En este
                   caso publishProgress() y onProgressUpdate() recibirán como parámetros datos de
                   tipo entero (Integer).
      Parametro 3: El tipo de datos que devolveremos como resultado de nuestra tarea, que será el
                   tipo de retorno del método doInBackground() y el tipo del parámetro recibido en
                   el método onPostExecute(). En este caso doInBackground() devolverá como retorno
                   un dato de tipo booleano y onPostExecute() también recibirá como parámetro un
                   dato del dicho tipo (Boolean).
*/
public class clsAsincrono  extends AsyncTask<Void, Integer, Boolean> {

    ProgressBar pbarProgreso;
    private Activity activity;

    public clsAsincrono(Activity activity,ProgressBar pbarProgreso) {
        this.activity = activity;
        this.pbarProgreso = pbarProgreso;
    }


    private void detenerHilo()
    {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {}
    }


    /*Antes de iniciar el proceso, reiniciamos la barra de progreso*/
    @Override
    protected void onPreExecute() {
        pbarProgreso.setMax(100);
        pbarProgreso.setProgress(0);
    }


    /*
    * El método doInBackground() se ejecuta en un hilo secundario (por tanto no podremos interactuar
    * con la interfaz), pero sin embargo todos los demás se ejecutan en el hilo principal, lo que
    * quiere decir que dentro de ellos podremos hacer referencia directa a nuestros controles de
    * usuario para actualizar la interfaz. Ademas si se llama al metodo publishProgress() este
    * automáticamente ejecuta el onProgressUpdate() que actualizara la interfaz si es necesario
    * */
    @Override
    protected Boolean doInBackground(Void... params) {

        for(int i=1; i<=10; i++) {
            detenerHilo();
            /*Se llama a la funcion onProgressUpdate para actualizar la interfaz*/
            publishProgress(i*10);
            /*Se valida si se ha solicitado finalizar el proceso*/
            if(isCancelled())
                /*Se llama a la funcion onCancelled*/
                break;
        }

        /*Se le retorna true a la funcion onPostExecute*/
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /*Se saca el progreso mandado y se actualiza en la barra de progreso */
        int progreso = values[0].intValue();
        pbarProgreso.setProgress(progreso);
    }


    @Override
    protected void onPostExecute(Boolean result) {
        /*Tan pronto termine el proceso, se muestra un toast en la activity indincando que termino
        * el proceso*/
        if(result)
            Toast.makeText(activity, "Tarea finalizada desde la clase asincrona!",
                    Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        /*Si se cancela el proceso se le indica al usuario*/
        Toast.makeText(activity, "Tarea cancelada!",
                Toast.LENGTH_SHORT).show();
    }

}
