import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class AspectJWeaverPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        boolean hasApp = project.getPlugins().hasPlugin(AppPlugin.class)
        if (hasApp) {
            def appExtension = project.getExtensions().getByType(AppExtension.class)
            appExtension.registerTransform(new AspectJTransform(), Collections.EMPTY_LIST)
        }
    }
}