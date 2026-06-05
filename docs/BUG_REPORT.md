## Fixed Bugs

### Bug #1
Issue:
Profile screen crash

Root Cause:
Null state

Solution:
Added default state

Status:
Fixed

---

### Bug #2
Issue:
Vocabulary search not refreshing

Root Cause:
StateFlow not updating

Solution:
Trigger recomposition

Status:
Fixed

---

### Bug #3
Issue:
Navigation stack duplication

Root Cause:
Route pushed repeatedly

Solution:
Use launchSingleTop

Status:
Fixed
