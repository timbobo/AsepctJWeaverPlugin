import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.ide.common.internal.WaitableExecutor
import org.apache.commons.io.FileUtils

class AspectJTransform extends Transform {

    WaitableExecutor waitableExecutor = WaitableExecutor.useGlobalSharedThreadPool()

    @Override
    String getName() {
        return Constants.TRANSFORM_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)


        //等待所有任务结束
        waitableExecutor.waitForTasksWithQuickFail(true)
    }

    void processJarInputWithIncremental(JarInput jarInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        File dest = outputProvider.getContentLocation(
                jarInput.getFile().getAbsolutePath(),
                jarInput.getContentTypes(),
                jarInput.getScopes(),
                Format.JAR)
        if (isIncremental) {
            //处理增量编译
            processJarInputWhenIncremental(jarInput, dest)
        } else {
            //不处理增量编译
            processJarInput(jarInput, dest)
        }
    }

    void processJarInput(JarInput jarInput, File dest) {
        transformJarInput(jarInput, dest)
    }

    void processJarInputWhenIncremental(JarInput jarInput, File dest) {
        switch (jarInput.status) {
            case Status.NOTCHANGED:
                break
            case Status.ADDED:
            case Status.CHANGED:
                //处理有变化的
                transformJarInputWhenIncremental(jarInput.getFile(), dest, jarInput.status)
                break
            case Status.REMOVED:
                //移除Removed
                if (dest.exists()) {
                    FileUtils.forceDelete(dest)
                }
                break
        }
    }

    void transformJarInputWhenIncremental(JarInput jarInput, File dest, Status status) {
        if (status == Status.CHANGED) {
            //Changed的状态需要先删除之前的
            if (dest.exists()) {
                FileUtils.forceDelete(dest)
            }
        }
        //真正transform的地方
        transformJarInput(jarInput, dest)
    }

    void transformJarInput(JarInput jarInput, File dest) {
        //TODO do some transform
        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyFile(jarInput.getFile(), dest)
    }

    void processDirectoryInputWithIncremental(DirectoryInput directoryInput, TransformOutputProvider outputProvider, boolean isIncremental) {
        File dest = outputProvider.getContentLocation(
                directoryInput.getFile().getAbsolutePath(),
                directoryInput.getContentTypes(),
                directoryInput.getScopes(),
                Format.DIRECTORY)
        if (isIncremental) {
            //处理增量编译
            processDirectoryInputWhenIncremental(directoryInput, dest)
        } else {
            processDirectoryInput(directoryInput, dest)
        }
    }

    void processDirectoryInputWhenIncremental(DirectoryInput directoryInput, File dest) {
        FileUtils.forceMkdir(dest)
        String srcDirPath = directoryInput.getFile().getAbsolutePath()
        String destDirPath = dest.getAbsolutePath()
        Map<File, Status> fileStatusMap = directoryInput.getChangedFiles()
        fileStatusMap.each { Map.Entry<File, Status> entry ->
            File inputFile = entry.getKey()
            Status status = entry.getValue()
            String destFilePath = inputFile.getAbsolutePath().replace(srcDirPath, destDirPath)
            File destFile = new File(destFilePath)
            switch (status) {
                case Status.NOTCHANGED:
                    break
                case Status.REMOVED:
                    if (destFile.exists()) {
                        FileUtils.forceDelete(destFile)
                    }
                    break
                case Status.ADDED:
                case Status.CHANGED:
                    FileUtils.touch(destFile)
                    transformSingleFile(inputFile, destFile, srcDirPath)
                    break
            }
        }
    }

    void processDirectoryInput(DirectoryInput directoryInput, File dest) {
        transformDirectoryInput(directoryInput, dest)
    }

    void transformDirectoryInput(DirectoryInput directoryInput, File dest) {
        //TODO do some transform
        //将修改过的字节码copy到dest，就可以实现编译期间干预字节码的目的了
        FileUtils.copyDirectory(directoryInput.getFile(), dest)
    }

    void transformSingleFile(File inputFile, File destFile, String srcDirPath) {
        FileUtils.copyFile(inputFile, destFile)
    }
}