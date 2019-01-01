import org.gradle.api.Project
import com.android.builder.model.AndroidProject

class FileFilter {

    String basePath

    String aspectDirPath

    String includeDirPath

    String excludeDirPath

    FileFilter(Project project, String variantName) {
        basePath = project.buildDir.absolutePath + File.separator + AndroidProject.FD_INTERMEDIATES +
                File.separator + Constants.TRANSFORM_NAME + File.separator + variantName
        aspectDirPath = basePath + File.separator + "aspects"
        includeDirPath = basePath + File.separator + "include_dir"
        excludeDirPath = basePath + File.separator + "exclude_dir"
    }

}