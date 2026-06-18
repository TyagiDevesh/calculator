# Quick Copy-Paste Prompt Template for Code-Assist Tools

Use this as the direct prompt text to feed into another code-assist tool for rapid SonarCloud setup.

---

## PROMPT TEXT (Copy Everything Below This Line)

```
TASK: Enable SonarCloud CI/CD for a Maven-based project

PLACEHOLDERS TO FILL IN FIRST:
  <REPO_NAME>              = your-repo-name (e.g., calculator, api-service)
  <DEFAULT_BRANCH>         = main (or master)
  <SONAR_ORG>              = your SonarCloud organization name
  <SONAR_PROJECT_KEY>      = your SonarCloud project key (e.g., myorg_myrepo)
  <SONAR_PROJECT_NAME>     = Your Project Display Name
  <JDK_VERSION>            = 21 (or 17, or 11)
  <JAVA_SOURCE_TARGET>     = 21 (same as JDK_VERSION)

STEP 1: INSPECT EXISTING CONFIG
  [ ] Read pom.xml and document:
      - Does it have Spring Boot parent?
      - Does it have Sonar properties?
      - Does it have jacoco-maven-plugin?
      - Does it have maven-surefire-plugin?
      - Are there any old org.springframework:spring-web* dependencies?
  [ ] Check for .github/workflows/ existing workflows
  
STEP 2: UPDATE pom.xml
  [ ] Add Spring Boot parent (if not present):
      <parent>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-parent</artifactId>
          <version>3.2.4</version>
          <relativePath/>
      </parent>

  [ ] Ensure <properties> section contains:
      <sonar.projectKey><SONAR_PROJECT_KEY></sonar.projectKey>
      <sonar.organization><SONAR_ORG></sonar.organization>
      <sonar.host.url>https://sonarcloud.io</sonar.host.url>
      <sonar.projectName><SONAR_PROJECT_NAME></sonar.projectName>
      <maven.compiler.source><JAVA_SOURCE_TARGET></maven.compiler.source>
      <maven.compiler.target><JAVA_SOURCE_TARGET></maven.compiler.target>

  [ ] CRITICAL: Remove any conflicting dependencies like:
      <dependency>
          <groupId>org.springframework</groupId>
          <artifactId>spring-web</artifactId>
          <version>4.3.0.RELEASE</version> <!-- DELETE THIS -->
      </dependency>

  [ ] Ensure <build><plugins> contains jacoco-maven-plugin:
      <plugin>
          <groupId>org.jacoco</groupId>
          <artifactId>jacoco-maven-plugin</artifactId>
          <version>0.8.15</version>
          <executions>
              <execution>
                  <goals><goal>prepare-agent</goal></goals>
              </execution>
              <execution>
                  <id>report</id>
                  <phase>test</phase>
                  <goals><goal>report</goal></goals>
              </execution>
          </executions>
      </plugin>

  [ ] Ensure <build><plugins> contains maven-surefire-plugin:
      <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>3.0.0</version>
      </plugin>

STEP 3: CREATE GITHUB ACTIONS WORKFLOW
  [ ] Create file: .github/workflows/sonarcloud.yml
      
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
                fetch-depth: 0

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

            - name: Upload coverage to Codecov (optional)
              uses: codecov/codecov-action@v4
              with:
                files: ./target/site/jacoco/jacoco.xml
                fail_ci_if_error: false
              env:
                CODECOV_TOKEN: ${{ secrets.CODECOV_TOKEN }}

STEP 4: VALIDATE LOCALLY
  [ ] Run: mvn clean verify
      Expected: [INFO] BUILD SUCCESS, Tests passed (0 errors)
  
  [ ] Verify: target/site/jacoco/jacoco.xml exists
      Command: ls -la target/site/jacoco/jacoco.xml
      Expected: File size > 20KB

STEP 5: CONFIGURE GITHUB SECRETS
  [ ] Go to GitHub repo → Settings → Secrets and variables → Actions
  [ ] Add new secret:
      Name: SONAR_TOKEN
      Value: (Get from https://sonarcloud.io/account/security)

STEP 6: COMMIT AND PUSH
  [ ] git add .
  [ ] git add .github/workflows/sonarcloud.yml
  [ ] git add pom.xml
  [ ] git commit -m "feat: Enable SonarCloud CI/CD pipeline"
  [ ] git push origin <DEFAULT_BRANCH>

STEP 7: VERIFY
  [ ] Go to GitHub Actions → Check workflow status
  [ ] Wait for completion (~5-7 minutes first run)
  [ ] Check SonarCloud: https://sonarcloud.io/projects/<SONAR_PROJECT_KEY>

TROUBLESHOOTING:
  Issue: Tests fail with ApplicationContext error
    → Remove old org.springframework:spring-web from pom.xml
    → Re-run mvn clean verify
  
  Issue: SONAR_TOKEN not set
    → Add secret to GitHub repo settings
    → Re-run workflow
  
  Issue: SonarCloud project not found
    → Verify project exists in SonarCloud
    → Verify <SONAR_PROJECT_KEY> matches pom.xml

SUCCESS INDICATORS:
  ✓ mvn clean verify passes locally
  ✓ target/site/jacoco/jacoco.xml generated
  ✓ GitHub Actions workflow completes
  ✓ SonarCloud shows project dashboard
  ✓ Coverage metrics display
```

---

## HOW TO USE THIS PROMPT

1. Copy the text between the ``` markers above
2. Fill in all `<...>` placeholders with actual values
3. Paste into your code-assist tool (Claude, ChatGPT, etc.)
4. Add instruction: "Follow this template step-by-step. Execute each action. Report completion."

---

## EXAMPLE FILLED-IN PROMPT

Here's an example with real values (you'll modify with your own):

```
TASK: Enable SonarCloud CI/CD for a Maven-based project

PLACEHOLDERS TO FILL IN FIRST:
  <REPO_NAME>              = payment-api
  <DEFAULT_BRANCH>         = main
  <SONAR_ORG>              = mycompany
  <SONAR_PROJECT_KEY>      = mycompany_payment-api
  <SONAR_PROJECT_NAME>     = Payment API Service
  <JDK_VERSION>            = 21
  <JAVA_SOURCE_TARGET>     = 21

[... rest of prompt continues with values substituted ...]
```

---

## QUICK COMMAND REFERENCE

After setup, these commands validate the pipeline:

```powershell
# Build locally
mvn clean verify

# Check coverage report
if (Test-Path "target/site/jacoco/jacoco.xml") { Write-Host "✅ Coverage found" }

# Test Sonar connection (requires SONAR_TOKEN env var)
$env:SONAR_TOKEN = "your-token"
mvn -B sonar:sonar -Dsonar.projectKey=<KEY> -Dsonar.organization=<ORG> -DskipTests

# Push to trigger workflow
git add .
git commit -m "feat: Enable SonarCloud CI/CD"
git push origin main
```

---

Created: June 13, 2026

