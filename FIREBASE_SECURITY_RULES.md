# Firebase Realtime Database Security Rules Guide

## Your Rules (Production-Ready & Secure)

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    },
    "admins": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
      }
    }
  }
}
```

---

## What These Rules Do

### `users` Data:
- ✅ **User can READ** their own data: `/users/{their-uid}`
- ✅ **User can WRITE** to their own data: `/users/{their-uid}`
- ❌ **User CANNOT** read other users' data
- ❌ **User CANNOT** write to other users' data

### `admins` Data:
- ✅ **Admin can READ** their own admin data: `/admins/{their-uid}`
- ✅ **Admin can WRITE** to their own admin data: `/admins/{their-uid}`
- ❌ **Admin CANNOT** access other admins' data

### Example:

**User with UID: `abc123`:**
- ✅ Can access: `/users/abc123/phone`, `/users/abc123/name`
- ❌ Cannot access: `/users/xyz789/phone` (other user's data)

---

## How This Works with Your Code

### Current Code Structure:
```java
DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

// PhoneLoginActivity.java - Finding user by phone
Query query = usersRef.orderByChild("phone").equalTo(phoneNumber);
```

### Problem:
Your current code queries **all** users to find a match. With these rules, each user can only see their own data. You need to authenticate first!

---

## ⚠️ IMPORTANT: You Need Firebase Authentication

### Current Issue:
Your app stores users manually without Firebase Authentication. These rules require `auth.uid`, which only exists when a user is authenticated through Firebase Auth.

### Solutions:

#### Option 1: Use Firebase Phone Authentication (Recommended)
Your app already has `PhoneLoginActivity` which should use Firebase Auth. Make sure:

```java
// In OTPVerificationActivity.java
FirebaseAuth.getInstance().signInWithCredential(credential)
    .addOnSuccessListener(authResult -> {
        // User is now authenticated
        // Get their UID
        String uid = authResult.getUser().getUid();
        
        // Now they can access their data under /users/{uid}
    });
```

#### Option 2: Temporarily Use Public Rules (Not Recommended)
For testing only:
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

#### Option 3: Custom Authentication (Advanced)
Store custom tokens and verify them (requires backend).

---

## Database Structure with These Rules

### Recommended Structure:

```
database
├── users/
│   ├── {user-uid-1}/
│   │   ├── phone: "9876543210"
│   │   ├── name: "John Doe"
│   │   ├── email: "john@example.com"
│   │   └── createdAt: 1234567890
│   │
│   └── {user-uid-2}/
│       ├── phone: "9123456789"
│       ├── name: "Jane Smith"
│       └── email: "jane@example.com"
│
├── admins/
│   ├── {admin-uid-1}/
│   │   ├── email: "admin@example.com"
│   │   ├── permissions: ["read", "write", "delete"]
│   │   └── createdAt: 1234567890
```

---

## Changes Needed in Your App

### 1. Update PhoneLoginActivity to Store User with Auth UID

**Before (Current):**
```java
String userId = usersRef.push().getKey(); // Random key
userData.put("id", userId);
usersRef.child(userId).setValue(userData);
```

**After (With Auth):**
```java
String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
userData.put("id", uid); // Use auth UID
usersRef.child(uid).setValue(userData); // Store under auth UID
```

### 2. Update Login Query

**Before (Current):**
```java
Query query = usersRef.orderByChild("phone").equalTo(phoneNumber);
```

**After (Recommended):**
```java
// After user authenticates with OTP
String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
DatabaseReference userRef = usersRef.child(uid);
userRef.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        // User data exists for this UID
    }
});
```

---

## Step-by-Step: Implement Secure Authentication

### Step 1: Enable Phone Authentication in Firebase

1. Go to Firebase Console > Authentication
2. Click **Sign-in method**
3. Enable **Phone**
4. Add test phone numbers (optional for testing)

### Step 2: Update OTPVerificationActivity

```java
private void signInWithCredential(PhoneAuthCredential credential) {
    FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // User authenticated!
                FirebaseUser user = task.getResult().getUser();
                String uid = user.getUid();
                
                // Now save user data under their auth UID
                saveUserData(uid);
                
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });
}

private void saveUserData(String uid) {
    Map<String, Object> userData = new HashMap<>();
    userData.put("phone", phoneNumber);
    userData.put("name", userName);
    userData.put("createdAt", System.currentTimeMillis());
    
    FirebaseDatabase.getInstance()
        .getReference("users")
        .child(uid)
        .setValue(userData);
}
```

### Step 3: Update SignUpActivity

```java
private void registerUserInFirebase(String name, String phone, String password) {
    // Create Firebase Auth user with email/password
    FirebaseAuth.getInstance()
        .createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener(authResult -> {
            String uid = authResult.getUser().getUid();
            
            // Save user data under auth UID
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", name);
            userData.put("phone", phone);
            userData.put("createdAt", System.currentTimeMillis());
            
            FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid)
                .setValue(userData)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                });
        });
}
```

---

## Testing These Rules

### Test 1: Check Authentication Status
```java
FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
if (user != null) {
    String uid = user.getUid();
    Log.d("AUTH", "User UID: " + uid);
} else {
    Log.d("AUTH", "User not authenticated");
}
```

### Test 2: Read Your Own Data
```java
String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
DatabaseReference ref = FirebaseDatabase.getInstance()
    .getReference("users").child(uid);

ref.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        // Should work - reading own data
        String name = snapshot.child("name").getValue(String.class);
    }
});
```

### Test 3: Try to Read Other User's Data
```java
DatabaseReference ref = FirebaseDatabase.getInstance()
    .getReference("users").child("other-uid");

ref.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        // Should be EMPTY - can't read other user's data
        if (snapshot.exists()) {
            Log.d("ERROR", "Security breach!");
        } else {
            Log.d("GOOD", "Data protected - can't read other user");
        }
    }
});
```

---

## Benefits of These Rules

✅ **Secure**: Users can only access their own data  
✅ **Private**: Prevents data leaks between users  
✅ **Scalable**: Works as app grows  
✅ **Production-ready**: Safe to use in live app  
✅ **Compliant**: Follows security best practices  

---

## When to Use Each Rule Type

| Rules | When to Use |
|-------|------------|
| `".read": true, ".write": true` | Testing only, not secure |
| `"$uid === auth.uid"` | User authentication, secure |
| `"$uid === auth.uid OR root.child('admins').child(auth.uid).exists()"` | Admin/user roles |

---

## Migrating from Current Setup

### Step 1: Backup Data
Export all current user data from Firebase Console

### Step 2: Update Rules
Apply the new rules (can't read old data without auth)

### Step 3: Update Code
Update all activities to use Firebase Authentication

### Step 4: Re-test
Verify everything works with new rules

---

## Common Issues & Solutions

### Issue: "Permission denied" after adding these rules
**Cause**: User not authenticated  
**Solution**: Implement Firebase Authentication

### Issue: Can't access own data
**Cause**: UID doesn't match auth UID  
**Solution**: Use `FirebaseAuth.getInstance().getCurrentUser().getUid()`

### Issue: Can still access other users' data
**Cause**: Rules not published  
**Solution**: Click Publish button in Firebase Console

---

## Next Steps

1. ✅ Update Firebase Rules (already done)
2. ⏳ Enable Phone Authentication in Firebase Console
3. ⏳ Update OTPVerificationActivity to authenticate users
4. ⏳ Update SignUpActivity to create auth accounts
5. ⏳ Update PhoneLoginActivity to work with auth
6. ⏳ Test thoroughly with new rules

---

**Status**: Rules configured for security  
**Next Action**: Implement Firebase Authentication in app code
