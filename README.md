# PrintScript
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/Nestiii/PrintScript/Java%20CI%20with%20Gradle)

PrintScript project for Systems Engineering Universidad Austral

## Branching  Strategy

### Feature branch strategy

Feature branching strategy allows developers to create a branch for a specific feature or task. This branch-per-issue workflow allows developers to work separately.

It also helps developers easily segment their work. Instead of tackling an entire release, they can focus on small sets of changes. 

Also feature branches can be divided up according to specific feature groups. Each team or sub-team could maintain their own branch for development. Then when they are finished, changes can be tested and reviewed before integrating them together.

### New feature

Feature branches are used when developing a new feature or enhancement which has the potential of a development lifespan longer than a single deployment. When starting development, the deployment in which this feature will be released may not be known. No matter when the feature branch will be finished, it will always be merged back into the master branch.

During the lifespan of the feature development, the lead should watch the master branch (network tool or branch tool in GitHub) to see if there have been commits since the feature was branched. Any and all changes to master should be merged into the feature before merging back to master; this can be done at various times during the project or at the end, but time to handle merge conflicts should be accounted for.

Rules

 - Must branch from: master
 - Must merge back into: master
 - Branch naming convention: feature-[featureId or featureName]

### Bugs

Bug branches differ from feature branches only semantically. Bug branches will be created when there is a bug on the live site that should be fixed and merged into the next deployment. For that reason, a bug branch typically will not last longer than one deployment cycle. No matter when the bug branch will be finished, it will always be merged back into master.

Although likelihood will be less, during the lifespan of the bug development, the lead should watch the master branch (network tool or branch tool in GitHub) to see if there have been commits since the bug was branched. Any and all changes to master should be merged into the bug before merging back to master, this can be done at various times during the project or at the end, but time to handle merge conflicts should be accounted for.

Rules

 - Must branch from: master
 - Must merge back into: master
 - Branch naming convention: bug-[bugId or bugName]
 
 ### Hotfix
 
 A hotfix branch comes from the need to act immediately upon an undesired state of a live production version. Additionally, because of the urgency, a hotfix is not required to be be pushed during a scheduled deployment. Due to these requirements, a hotfix branch is always branched from a tagged stable branch. This is done for two reasons:
 
  - Development on the master branch can continue while the hotfix is being addressed.
  - A tagged stable branch still represents what is in production. At the point in time where a hotfix is needed, there could have been multiple commits to master which would then no longer represent production.
   
 Rules 
   
  - Must branch from: tagged stable
  - Must merge back into: master and stable
  - Branch naming convention: hotfix-[hotfixId or hotfixName]