package Simulator.ios;

import hudson.Launcher;
import hudson.Extension;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import java.io.BufferedReader;
import java.io.File;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this processBuilder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link HelloWorldBuilder} is created. The created instance is persisted to
 * the project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #name}) to remember the configuration.
 *
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be
 * invoked.
 *
 * @author Aryan Goharzad
 */
public class HelloWorldBuilder extends Builder {

    private final String name;
    private final String simulatorType, appLocation, numRetries, sleepTime, deviceName, deviceOS;
    private final Boolean shouldClose, shouldReset;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public HelloWorldBuilder(String name) {
        this.name = name;
        simulatorType = appLocation = numRetries = sleepTime = deviceName = deviceOS = "--";
        shouldClose = shouldReset = false;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return name;
    }

  /* 
    DELETE ME
    private static void printOutputs(Process proc, BuildListener listener) throws IOException {
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

// read the output from the command
        listener.getLogger().println("Here is the standard output of the command:\n");
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            listener.getLogger().println(s);
        }

// read any errors from the attempted command
        listener.getLogger().println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            listener.getLogger().println(s);
        }
    }*/

    private static BufferedReader getError(Process p) {
        return new BufferedReader(new InputStreamReader(p.getErrorStream()));
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        // This is where you 'build' the project.
        // Since this is a dummy, we just say 'hello world' and call that a build.
        

/* DELETE ME
         ProcessBuilder pb = new ProcessBuilder("myshellScript.sh", "myArg1", "myArg2");
         Map<String, String> env = pb.environment();
         env.put("VAR1", "myValue");
         env.remove("OTHERVAR");
         env.put("VAR2", env.get("VAR1") + "suffix");
         pb.directory(new File("myDir"));
         Process p = pb.start();

         */

        // This also shows how you can consult the global configuration of the processBuilder
        /*
         Runtime rt = Runtime.getRuntime();
         String[] commands = {"ios-sim launch \"/Users/aryanwork/Library/Developer/Xcode/DerivedData/snypr-gpllkdvetehpzhekopwuanebcprv/Build/Products/Debug-iphonesimulator/SNYPR.app\" --devicetypeid \"com.apple.CoreSimulator.SimDeviceType.iPhone-6, 8.1\" --exit"};
         Process proc = rt.exec(commands);

         commands = new String[]{"pwd"};
         proc = rt.exec(commands);

         commands = new String[]{"ls"};
         proc = rt.exec(commands);
         printOutputs(proc, listener);*/
        
        List<String> command = new ArrayList<String>();
        command.add("ls && pwd && cd .. && pwd");
        ProcessBuilder builder = new ProcessBuilder(command);
        Map<String, String> environ = builder.environment();
        Process process = builder.start();
        process.waitFor();
        
        
     
        
        List<String> command3 = new ArrayList<String>();
        command3.add("pwd");
        builder = new ProcessBuilder(command);
        process = builder.start();
        process.waitFor();
        
        StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", listener);

        // any output?
        StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", listener);

        // start gobblers
        outputGobbler.start();
        errorGobbler.start();
        
       
        listener.getLogger().println("Program terminated!");


        /*
         BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

         BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

         // read the output from the command
         System.out.println("Here is the standard output of the command:\n");
         String s = null;
         while ((s = stdInput.readLine()) != null) {
         listener.getLogger().println(s);
         }

         // read any errors from the attempted command
         System.out.println("Here is the standard error of the command (if any):\n");
         while ((s = stdError.readLine()) != null) {
         listener.getLogger().println(s);
         }*/
        listener.getLogger().println("Bonjour, " + name + "! :) this is harder than i thought 33");

        return true;

    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link HelloWorldBuilder}. Used as a singleton. The class
     * is marked as public so that it can be accessed from views.
     *
     * <p>
     * See
     * <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * To persist global configuration information, simply store it in a
         * field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useFrench;

        /**
         * In order to load the persisted global configuration, you have to call
         * load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has
         * typed.
         * @return Indicates the outcome of the validation. This is sent to the
         * browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message will
         * be displayed to the user.
         */
        public FormValidation doCheckName(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a name");
            }
            if (value.length() < 4) {
                return FormValidation.warning("Isn't the name too short?");
            }
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this processBuilder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Say hello world";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }

        /**
         * This method returns true if the global configuration says we should
         * speak French.
         *
         * The method name is bit awkward because global.jelly calls this method
         * to determine the initial state of the checkbox by the naming
         * convention.
         */
        public boolean getUseFrench() {
            return useFrench;
        }
    }

    private class StreamGobbler extends Thread {

        InputStream is;
        String type;
        BuildListener listener;

        private StreamGobbler(InputStream is, String type, BuildListener listener) {
            this.is = is;
            this.type = type;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    listener.getLogger().println(type + "> " + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

}
