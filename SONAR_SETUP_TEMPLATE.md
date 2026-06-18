# Sonar CI/CD Setup Template - Reusable for Any Maven Project

> **Purpose**: Complete step-by-step template to enable SonarCloud CI/CD analysis on any Maven-based project
>
> **Copy this entire file and fill in the placeholders (marked with `<...>`) before using with a code-assist tool**

---

## Template Placeholders (REQUIRED - Fill these in first)

```
<REPO_NAME>              = your-repo-name (e.g., calculator, payment-service)
<DEFAULT_BRANCH>         = main or master
<GITHUB_ORG>             = your GitHub organization or username
<SONAR_ORG>              = your SonarCloud organization (from https://sonarcloud.io/account/organizations)
<SONAR_PROJECT_KEY>      = project key from SonarCloud (e.g., myorg_myrepo)
<SONAR_PROJECT_NAME>     = human-readable project name (e.g., My Awesome Project)
<JDK_VERSION>            = 21 or 17 or 11 (your project's Java version)
<JAVA_SOURCE_TARGET>     = same as JDK_VERSION
```

---

## Step 1: Discover Current State (Read-Only Inspection)

Before making any changes, inspect the target repository:

### Check for existing files and config:

```bash
# Look for SonarCloud config
grep -r "sonar.projectKey" pom.xml

# Check for JaCoCo setup
grep -r "jacoco-maven-plugin" pom.xml

# Check for test runner config
grep -r "maven-surefire-plugin" pom.xml

# Check for existing workflows
ls -la .github/workflows/
```

### Document findings as:
- **Already present**: List what exists
- **Missing**: List what needs to be added
- **Needs update**: List what conflicts exist

---

## Step 2: Maven/POM Updates

### 2.1 - Add Spring Boot Parent (if using Spring Boot)

**Location**: `pom.xml` → top level, before `<groupId>`

**Add this block if using Spring Boot, OR skip if not a Spring Boot project:**

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.4</version>
    <relativePath/>
</parent>
```

---

### 2.2 - Add Sonar Properties

**Location**: `pom.xml` → `<properties>` section

**Add or replace existing Sonar properties:**

```xml
<properties>
    <maven.compiler.source><JAVA_SOURCE_TARGET></maven.compiler.source>
    <maven.compiler.target><JAVA_SOURCE_TARGET></maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    
    <!-- SonarCloud Properties -->
    <sonar.projectKey><SONAR_PROJECT_KEY></sonar.projectKey>
    <sonar.organization><SONAR_ORG></sonar.organization>
    <sonar.host.url>https://sonarcloud.io</sonar.host.url>
    <sonar.projectName><SONAR_PROJECT_NAME></sonar.projectName>
</properties>
```

---

### 2.3 - Ensure JaCoCo Plugin (Code Coverage)

**Location**: `pom.xml` → `<build>` → `<plugins>`

**Add if missing:**

```xml
<!-- JaCoCo Plugin for Code Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.15</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

### 2.4 - Ensure Maven Surefire Plugin (Test Execution)

**Location**: `pom.xml` → `<build>` → `<plugins>`

**Add if missing:**

```xml
<!-- Maven Surefire Plugin for Running Tests -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0</version>
</plugin>
```

---

### 2.5 - CRITICAL: Resolve Dependency Conflicts

**Common issue**: Old explicit `org.springframework:spring-web` conflicts with Spring Boot 3+

**Action**: Search `pom.xml` for these patterns and REMOVE them if using Spring Boot 3.x:

```xml
<!-- REMOVE IF PRESENT (conflicts with Spring Boot 3.x) -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>4.3.0.RELEASE</version>  <!-- ❌ DELETE THIS BLOCK -->
</dependency>
```

**Why**: Spring Boot parent BOM manages Spring versions. Explicit old versions cause classpath conflicts.

---

## Step 3: Create GitHub Actions Workflow

### 3.1 - Create workflow directory and file

**Create file**: `.github/workflows/sonarcloud.yml`

```yaml
name: SonarCloud Analysis

on:
  push:
    branches: [ "<DEFAULT_BRANCH>" ]
  pull_request:
    branches: [ "<DEFAULT_BRANCH>" ]
  workflow_dispatch:

jobs:
  sonarcloud:
    name: SonarCloud Scan
    runs-on: ubuntu-latest
    permissions:
      contents: read
    
    steps:
      - name: Checkout code with full history
        uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Full history for accurate SonarCloud analysis

      - name: Set up JDK <JDK_VERSION>
        uses: actions/setup-java@v4
        with:
          java-version: '<JDK_VERSION>'
          distribution: 'temurin'
          cache: maven

      - name: Build and run tests with coverage
        run: mvn clean verify

      - name: Run SonarCloud scan
        run: |
          mvn -B sonar:sonar \
            -Dsonar.projectKey=<SONAR_PROJECT_KEY> \
            -Dsonar.organization=<SONAR_ORG>
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

      - name: Upload coverage reports to Codecov (optional)
        uses: codecov/codecov-action@v4
        with:
          files: ./target/site/jacoco/jacoco.xml
          fail_ci_if_error: false
        env:
          CODECOV_TOKEN: ${{ secrets.CODECOV_TOKEN }}
```

---

## Step 4: Configure Repository Secrets

### 4.1 - Add SONAR_TOKEN to GitHub

**Steps**:
1. Go to GitHub repo → **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. **Name**: `SONAR_TOKEN`
4. **Value**: Get from https://sonarcloud.io/account/security → Generate token
5. Click **Add secret**

### 4.2 - Verify SonarCloud Project Exists

**Steps**:
1. Log in to https://sonarcloud.io
2. Go to **Organizations** → select your org
3. Verify project exists with matching:
   - Organization: `<SONAR_ORG>`
   - Project Key: `<SONAR_PROJECT_KEY>`
4. If not present, create it in SonarCloud

---

## Step 5: Validate Locally

### 5.1 - Test Maven build locally

Run these commands in the project root:

```powershell
# Check Maven version
mvn -version

# Clean compile and test with coverage
mvn clean verify

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Tests run: X, Failures: 0, Errors: 0
```

### 5.2 - Verify JaCoCo coverage report generated

```powershell
# Check if coverage report exists
if (Test-Path "target/site/jacoco/jacoco.xml") {
    Write-Host "✅ JaCoCo coverage report found"
    Get-Item "target/site/jacoco/jacoco.xml" | Select-Object Length
} else {
    Write-Host "❌ Coverage report missing"
}
```

**Expected**: `target/site/jacoco/jacoco.xml` exists (size: 20KB+)

### 5.3 - Test SonarCloud connection (optional, requires SONAR_TOKEN)

```powershell
# Set token as environment variable
$env:SONAR_TOKEN = "your-token-from-sonarcloud"

# Run Sonar scan (skip tests for faster validation)
mvn -B sonar:sonar `
  -Dsonar.projectKey=<SONAR_PROJECT_KEY> `
  -Dsonar.organization=<SONAR_ORG> `
  -DskipTests

# Expected output:
# [INFO] ANALYSIS SUCCESSFUL
```

---

## Step 6: Deployment

### 6.1 - Commit changes locally

```bash
git add .
git add .github/workflows/sonarcloud.yml
git add pom.xml
git commit -m "feat: Enable SonarCloud CI/CD pipeline"
```

### 6.2 - Push to trigger first workflow run

```bash
git push origin <DEFAULT_BRANCH>
```

### 6.3 - Monitor workflow

1. Go to GitHub repo → **Actions** tab
2. Find "SonarCloud Analysis" workflow
3. Wait for it to complete (~5-7 minutes first run)
4. View results in SonarCloud: `https://sonarcloud.io/projects/<SONAR_PROJECT_KEY>`

---

## Step 7: Troubleshooting Common Issues

### Issue: Tests fail with "ApplicationContext" error

**Cause**: Dependency version conflict (Spring versions mismatch)

**Fix**:
1. Search `pom.xml` for explicit old `org.springframework:*` dependencies
2. Remove them if using Spring Boot 3.x
3. Let Spring Boot parent BOM manage versions
4. Re-run `mvn clean verify`

---

### Issue: "SONAR_TOKEN not configured"

**Cause**: GitHub secret not set

**Fix**:
1. Go to repo Settings → Secrets and variables → Actions
2. Add new secret named `SONAR_TOKEN`
3. Value: Token from https://sonarcloud.io/account/security
4. Re-run workflow

---

### Issue: "Project not found in SonarCloud"

**Cause**: Project doesn't exist or key is wrong

**Fix**:
1. Log in to SonarCloud
2. Create project manually or import from GitHub
3. Verify project key matches `<SONAR_PROJECT_KEY>` in `pom.xml`
4. Re-run workflow

---

### Issue: Coverage report not uploaded

**Cause**: Tests didn't run or JaCoCo not configured

**Fix**:
1. Verify `mvn clean verify` passes locally
2. Verify JaCoCo plugin exists in `pom.xml` with `prepare-agent` + `report`
3. Check that tests actually exist and run
4. Verify `target/site/jacoco/jacoco.xml` is created locally

---

## Step 8: Expected Workflow Behavior

After pushing to `<DEFAULT_BRANCH>`:

```
1. GitHub Actions triggered
   ↓
2. Code checked out (full history)
   ↓
3. Java <JDK_VERSION> environment set up
   ↓
4. Maven clean verify runs
   ├─ Compiles code
   ├─ Runs tests
   ├─ JaCoCo generates coverage
   └─ Creates JAR
   ↓
5. SonarCloud analysis runs
   ├─ Scans code for issues
   ├─ Uploads coverage metrics
   └─ Posts results
   ↓
6. Results available in:
   ✓ SonarCloud dashboard
   ✓ GitHub PR comments (if PR)
   ✓ GitHub Actions logs

Typical time: 5-7 minutes per run
```

---

## Summary Checklist

- [ ] Filled in all `<...>` placeholders
- [ ] Added Spring Boot parent BOM to `pom.xml`
- [ ] Added Sonar properties to `pom.xml`
- [ ] Added/verified JaCoCo plugin
- [ ] Added/verified Surefire plugin
- [ ] Removed conflicting old Spring dependencies (if applicable)
- [ ] Created `.github/workflows/sonarcloud.yml`
- [ ] Added `SONAR_TOKEN` secret to GitHub
- [ ] Verified SonarCloud project exists
- [ ] Ran `mvn clean verify` locally ✅
- [ ] Verified `target/site/jacoco/jacoco.xml` exists
- [ ] Pushed code to GitHub
- [ ] Workflow triggered successfully
- [ ] Results visible in SonarCloud

---

## Reference Files

### Complete pom.xml snippet (Spring Boot project with SonarCloud)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.4</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId><REPO_NAME></artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source><JAVA_SOURCE_TARGET></maven.compiler.source>
        <maven.compiler.target><JAVA_SOURCE_TARGET></maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <sonar.projectKey><SONAR_PROJECT_KEY></sonar.projectKey>
        <sonar.organization><SONAR_ORG></sonar.organization>
        <sonar.host.url>https://sonarcloud.io</sonar.host.url>
        <sonar.projectName><SONAR_PROJECT_NAME></sonar.projectName>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>3.2.4</version>
            </plugin>
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.15</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0</version>
            </plugin>
        </plugins>
    </build>

</project>
```

---

## How to Use This Template

1. **Copy this entire document**
2. **Replace all `<...>` placeholders** with actual values for your repository
3. **Paste into your code-assist tool** with instructions:
   - "Follow this template to set up SonarCloud CI/CD for repository XYZ"
   - "Implement all steps exactly as shown"
   - "Use these placeholder values: [list filled-in values]"
4. **Tool will**:
   - Create/update files
   - Run validations
   - Generate workflow
   - Configure secrets

---

## Questions to Ask Assistant Tool

Before starting:
- Does repo use Spring Boot? (if yes, version?)
- What Java version does project target?
- Are there existing tests?
- Does SonarCloud organization already exist?
- What is the default branch name?

---

**Created**: June 13, 2026  
**Version**: 1.0  
**Tested On**: Spring Boot 3.2.4 + Maven 3.9.12 + Java 21

