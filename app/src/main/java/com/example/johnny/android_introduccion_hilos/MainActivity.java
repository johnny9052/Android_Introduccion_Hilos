package com.example.johnny.android_introduccion_hilos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


/***********************
 * Teoria
 ************************/

/*
* Todos los componentes de una aplicación Android, tanto las actividades, los servicios, la interfaz
 * grafica se ejecutan en el mismo hilo de ejecución, el llamado hilo principal, main thread o GUI
 * thread. Es por ello, que cualquier operación larga o costosa que realicemos en este hilo va a
 * bloquear la ejecución del resto de componentes de la aplicación y por supuesto también la
 * interfaz, produciendo al usuario un efecto evidente de bloqueo. Incluso puede ser peor, dado que
  * Android monitoriza las operaciones realizadas en el hilo principal y detecta aquellas que
  * superen los 5 segundos, en cuyo caso se muestra el famoso mensaje de “Application Not
  * Responding” (ANR) y el usuario debe decidir entre forzar el cierre de la aplicación o esperar a
  * que termine.
* */


/**
 * Como utilizar el primer boton: Si solo se presiona el boton y se deja la aplicacion quieta, puede
 * durar entre 10 y 12 segundos, pero si se presiona la pantalla mientras el proceso ocurre ese tiempo
 * aumenta drasticamente o hasta se bloquea el app
 */


public class MainActivity extends AppCompatActivity {

    ProgressBar pbarProgreso; //Referencia para barra de progreso
    ProgressBar pbarProgreso2; //Referencia para barra de progreso
    clsAsincrono tarea;  //Referencia para clase asincrona

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Se referencia la barra de progreso*/
        pbarProgreso = (ProgressBar) findViewById(R.id.progressBar);
        pbarProgreso2 = (ProgressBar) findViewById(R.id.progressBar2);
        /*Se instancia la clase asincrona, mandando como parametro la clase que la llama, como
        * tambien la barra de progreso que sera modificada desde el hilo*/
    }


    /*Reinicia la barra de progreso en 0 */
    public void limpiar(View view) {
        pbarProgreso.setProgress(0);
    }

    /*Detenemos el hilo principal durante 1 segundo*/
    private void detenerHilo() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //Si sucede error se le indica al usuario
        }
    }


    /**************USO DEL HILO PRINCIPAL***********************************/
    /*******************Oppcion no recomendada, ya que bloquea la app*******/

    public void clickUsoHiloPrincipal(View view) {
        /*Se establece el progress bar en 100 como maximo*/
        pbarProgreso.setMax(100);
        /*Se establece su posicion actual en 0*/
        pbarProgreso.setProgress(0);

        for (int i = 1; i <= 10; i++) {
            /*Se detiene el hilo 1 segundo*/
            detenerHilo();
            /*Aumenta su progreso */
            pbarProgreso.incrementProgressBy(10);
        }

        Toast.makeText(MainActivity.this, "Tarea finalizada!",
                Toast.LENGTH_SHORT).show();
    }

    /**************END USO DEL HILO PRINCIPAL***********************************/


    /**************CREACION DE UN HILO BASICO***********************************/
    /**Oppcion no recomendada, ya que es dificil de mantener, realizar modificaciones
     * y cuando es mucho codigo es complejo de entender *******/


    /*Este hilo por si mismo no tiene acceso a los componentes del hilo principal, como la interfaz
    * grafica, para acceder a estos recursos se puede utilizar el metodo post. Tambien esta el
    * metodo runOnUiThread() para enviar operaciones al hilo principal*/
    public void clickHiloSinAccesoAOtrosRecursos(View view) {
        new Thread(new Runnable() {
            public void run() {

                /*Con el metodo post accedemos al progress bar*/
                pbarProgreso.post(new Runnable() {
                    public void run() {
                        pbarProgreso.setProgress(0);
                    }
                });

                /*Realizamos el proceso*/
                for (int i = 1; i <= 10; i++) {
                    detenerHilo();
                    /*Con el metodo post accedemos al progress bar*/
                    pbarProgreso.post(new Runnable() {
                        public void run() {
                            pbarProgreso.incrementProgressBy(10);
                        }
                    });
                }

                /*Con el metodo runOnUiThread mostramos el toast en el hilo principal*/
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Tarea finalizada!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }


    /************** USO DE LA CLASE ASYNCTASK, OPCION RECOMENDADA*********************************/

    /* la clase AsyncTask, permiti realizar lo del boton anterior pero con la ventaja de no tener
    que utilizar artefactos del tipo runOnUiThread() y de una forma mucho más organizada y legible.
    La forma básica de utilizar la clase AsyncTaskconsiste en crear una nueva clase que extienda de
    ella y sobrescribir varios de sus métodos entre los que repartiremos la funcionalidad de
    nuestra tarea.
    * */

    /*Se inicia el hilo*/
    public void clickClaseAsincrona(View view) {
        tarea = new clsAsincrono(MainActivity.this, pbarProgreso);
        tarea.execute();
    }

    /*Se detiene el hilo*/
    public void clickCancelarClaseAsincrona(View view) {
        tarea.cancel(true);
    }

    /**************END USO DE LA CLASE ASYNCTASK, OPCION RECOMENDADA*****************************/


    public void clickClaseAsincronaVentana(View view) {
        /*Se define un progressDialog*/
        ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
        /*Se define de estilo horizontal o el que se desee*/
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        /*Mensaje del dialog*/
        pDialog.setMessage("Procesando...");
        /*Se determina si la ventana puede ser cerrada por el usuario*/
        pDialog.setCancelable(false);
        /*Valor maximo del proceso*/
        pDialog.setMax(100);
        /*Se muestra el dialog*/
        pDialog.show();
        /*Se define una instancia de la clase*/
        clsAsincronoVentana proceso = new clsAsincronoVentana(MainActivity.this, pDialog);
        /*Se ejecuta el hilo*/
        proceso.execute();
    }


    public void clickHilosMultiples(View view){


        /*Se definen los hilos*/
        clsAsincrono tarea1 = new clsAsincrono(MainActivity.this, pbarProgreso);
        clsAsincrono tarea2 = new clsAsincrono(MainActivity.this, pbarProgreso2);

        /*Se valida si la version es mayor a honeycomb (3.0)*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            /*Si es mayor lo metemos al pool de hilos, (Son maximo 5)*/
            /*executeOnExecutor(PoolDeHilos, parametros si se desean mandar)*/
            tarea1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
            tarea2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,null);
        } else {
            /*Si no ejecute los hilos normalmente.*/
            tarea1.execute();
            tarea2.execute();
        }
    }

}
