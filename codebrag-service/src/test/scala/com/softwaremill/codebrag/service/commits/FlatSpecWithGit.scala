package com.softwaremill.codebrag.service.commits

import org.scalatest.{BeforeAndAfter, FlatSpec}
import org.scalatest.matchers.ShouldMatchers
import java.io.{FileOutputStream, PrintWriter, File}
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.{PersonIdent, Constants}
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.util.FileUtils
import com.softwaremill.codebrag.service.commits.jgit.InternalGitDirTree
import com.google.common.io.Files
import java.nio.file.Path

trait FlatSpecWithGit extends FlatSpec with BeforeAndAfter with ShouldMatchers {

  var testRepo: File = _

  def testRepoPath = testRepo.getCanonicalPath

  def initRepo(): File = {
    val dir = Files.createTempDir()
    Git.init().setDirectory(dir).setBare(false).call()
    val repo = new File(dir, Constants.DOT_GIT)
    repo.exists() should be(true)
    repo.deleteOnExit()
    repo
  }

  def givenCommit(path: String, content: String, message: String): RevCommit = {
    commitFile(testRepo, path, content, message)
  }

  def givenCommitAppending(path: String, content: String, message: String): RevCommit = {
    commitFile(testRepo, path, content, message, append = true)
  }

  val author = new PersonIdent("Sofokles", "sofokles@softwaremill.com")
  val committer = new PersonIdent("Bruce", "bruce@softwaremill.com")

  def commitFile(repo: File, path: String, content: String, message: String, append: Boolean = false): RevCommit = {
    val file = new File(repo.getParentFile, path)
    if (!file.getParentFile.exists())
      file.getParentFile.mkdirs() should be(true)
    if (!file.exists())
      file.createNewFile() should be(true)
    val writer = new PrintWriter(new FileOutputStream(file, append))
    try {
      writer.print(content)
    } finally {
      writer.close()
    }
    val git = Git.open(repo)
    git.add().addFilepattern(path).call()
    val commit = git.commit().setOnly(path).setMessage(message)
      .setAuthor(author).setCommitter(committer).call()
    commit should not be (null)
    commit
  }

  def deleteRootDirectoryRecursively() {
    FileUtils.delete(new InternalGitDirTree(TestCodebragAndRepositoryConfig).root.toFile, FileUtils.RECURSIVE | FileUtils.SKIP_MISSING)
  }

  object TestRepoData extends RepoData {
    override def remoteUri = testRepoPath
    def localPathRelativeTo(path: Path) = path.resolve("something")
    def credentials = new UsernamePasswordCredentialsProvider("codebrag-user", "")
    def credentialsValid = true
    def repositoryName = "something"
  }
}