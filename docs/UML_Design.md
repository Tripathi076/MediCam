# MediCam - UML Class Diagram Design

## Application Overview
MediCam is a medical records management Android application that allows users to manage pathology reports, radiology reports, BMI tracking, ABHA (Ayushman Bharat Health Account) registration, and device connectivity.

---

## Class Diagram (PlantUML Format)

```plantuml
@startuml MediCam_Class_Diagram

skinparam classAttributeIconSize 0
skinparam class {
    BackgroundColor #f5f5f5
    BorderColor #333333
    ArrowColor #333333
}

' =============================================
' BASE CLASSES & FRAMEWORK
' =============================================
abstract class AppCompatActivity {
    # onCreate(Bundle)
    # onResume()
    # onPause()
    + finish()
    + startActivity(Intent)
}

' =============================================
' UTILITY CLASSES
' =============================================
package "utils" #DDFFDD {
    
    class SessionManager <<Singleton>> {
        - {static} instance : SessionManager
        - encryptedSharedPreferences : SharedPreferences
        - {static} PREFS_NAME : String = "medicam_session"
        - {static} KEY_USER_ID : String
        - {static} KEY_USER_EMAIL : String
        - {static} KEY_USER_PHONE : String
        - {static} KEY_USER_NAME : String
        - {static} KEY_IS_LOGGED_IN : String
        - {static} KEY_AUTH_TOKEN : String
        - {static} KEY_REFRESH_TOKEN : String
        - {static} KEY_ABHA_NUMBER : String
        - {static} KEY_SESSION_TIMESTAMP : String
        --
        - SessionManager(Context)
        + {static} getInstance(Context) : SessionManager
        + saveUserSession(String, String, String, String)
        + saveAuthTokens(String, String)
        + saveABHANumber(String)
        + getUserId() : String
        + getUserEmail() : String
        + getUserPhone() : String
        + getUserName() : String
        + getAuthToken() : String
        + getRefreshToken() : String
        + getABHANumber() : String
        + isUserLoggedIn() : boolean
        + getSessionTimestamp() : long
        + clearSession()
        + isSessionValid() : boolean
    }
    
    class FirebaseErrorHandler <<Utility>> {
        - {static} TAG : String = "FirebaseErrorHandler"
        --
        - FirebaseErrorHandler()
        + {static} getAuthErrorMessage(Exception) : String
        + {static} getFirestoreErrorMessage(Exception) : String
        + {static} logException(String, Exception)
        + {static} isNetworkError(Exception) : boolean
    }
    
    class SessionInitializationException {
        + SessionInitializationException(String, Throwable)
    }
}

SessionManager +-- SessionInitializationException

' =============================================
' MODEL CLASSES
' =============================================
package "models" #FFDDDD {
    
    class PathologyReport {
        - labName : String
        - testName : String
        - collectionDate : String
        - doctorName : String
        - patientName : String
        - reportImageUri : String
        - category : String
        --
        + PathologyReport()
        + PathologyReport(String, String, String, String, String, String, String)
        + getLabName() : String
        + setLabName(String)
        + getTestName() : String
        + setTestName(String)
        + getCollectionDate() : String
        + setCollectionDate(String)
        + getDoctorName() : String
        + setDoctorName(String)
        + getPatientName() : String
        + setPatientName(String)
        + getReportImageUri() : String
        + setReportImageUri(String)
        + getCategory() : String
        + setCategory(String)
    }
}

' =============================================
' ADAPTER CLASSES
' =============================================
package "adapters" #DDDDFF {
    
    abstract class "RecyclerView.Adapter<VH>" as RecyclerViewAdapter {
        + onCreateViewHolder(ViewGroup, int) : VH
        + onBindViewHolder(VH, int)
        + getItemCount() : int
    }
    
    class PathologyReportAdapter {
        - context : Context
        - reports : List<PathologyReport>
        - filteredReports : List<PathologyReport>
        --
        + PathologyReportAdapter(Context, List<PathologyReport>)
        + onCreateViewHolder(ViewGroup, int) : ReportViewHolder
        + onBindViewHolder(ReportViewHolder, int)
        + getItemCount() : int
        + filter(String)
    }
    
    class ReportViewHolder {
        + ivReportThumbnail : ImageView
        + tvLabName : TextView
        + tvTestName : TextView
        + tvDate : TextView
        + tvDoctorName : TextView
        --
        + ReportViewHolder(View)
    }
}

PathologyReportAdapter --|> RecyclerViewAdapter
PathologyReportAdapter +-- ReportViewHolder
PathologyReportAdapter --> PathologyReport : uses

' =============================================
' AUTHENTICATION ACTIVITIES
' =============================================
package "authentication" #FFFFDD {
    
    class MainActivity {
        - {static} TAG : String = "MainActivity"
        - mAuth : FirebaseAuth
        --
        # onCreate(Bundle)
    }
    
    class SplashActivity {
        --
        # onCreate(Bundle)
    }
    
    class SignUpActivity {
        - nameEditText : TextInputEditText
        - phoneEditText : TextInputEditText
        - passwordEditText : TextInputEditText
        - stateAutoComplete : AutoCompleteTextView
        - cbTerms : CheckBox
        - btnSignUpAction : MaterialButton
        - tvSignIn : TextView
        --
        # onCreate(Bundle)
    }
    
    class PhoneLoginActivity {
        - {static} TAG : String = "PhoneLoginActivity"
        - etPhoneNumber : EditText
        - btnGetOTP : MaterialButton
        - tvAdminPortal : TextView
        - progressBar : ProgressBar
        - mAuth : FirebaseAuth
        - mCallbacks : OnVerificationStateChangedCallbacks
        - mVerificationId : String
        - mResendToken : ForceResendingToken
        --
        # onCreate(Bundle)
        - setupPhoneAuthCallbacks()
        - setupListeners()
        - signInWithCredential(PhoneAuthCredential)
    }
    
    class OTPVerificationActivity {
        - {static} TAG : String = "OTPVerificationActivity"
        - etOTP1, etOTP2, etOTP3, etOTP4 : EditText
        - btnVerifyOTP : MaterialButton
        - progressBar : ProgressBar
        - phoneNumber : String
        - verificationId : String
        - countDownTimer : CountDownTimer
        - mAuth : FirebaseAuth
        --
        # onCreate(Bundle)
        - maskPhoneNumber(String) : String
        - setupOTPInputs()
        - startResendTimer()
    }
    
    class AdminLoginActivity {
        - {static} TAG : String = "AdminLoginActivity"
        - mAuth : FirebaseAuth
        - progressBar : ProgressBar
        - emailEditText : TextInputEditText
        - passwordEditText : TextInputEditText
        --
        # onCreate(Bundle)
        - loginWithEmail(String, String)
        - showLoginSuccessDialog()
    }
    
    class LoginSignUpActivity {
        - nameEditText : TextInputEditText
        - emailEditText : TextInputEditText
        - passwordEditText : TextInputEditText
        - stateAutoComplete : AutoCompleteTextView
        - cbTerms : CheckBox
        --
        # onCreate(Bundle)
        - showSuccessDialog()
    }
    
    class ForgotPasswordActivity {
        - {static} TAG : String = "ForgotPasswordActivity"
        - progressBar : ProgressBar
        - mAuth : FirebaseAuth
        - etEmail : EditText
        --
        # onCreate(Bundle)
        - sendPasswordResetEmail(String)
    }
    
    class ResetPasswordOTPActivity {
        --
        # onCreate(Bundle)
    }
    
    class CreateNewPasswordActivity {
        --
        # onCreate(Bundle)
    }
    
    class PasswordResetSuccessActivity {
        --
        # onCreate(Bundle)
    }
    
    class LoginSuccessActivity {
        --
        # onCreate(Bundle)
    }
    
    class LoginUnsuccessActivity {
        --
        # onCreate(Bundle)
    }
}

MainActivity --|> AppCompatActivity
SplashActivity --|> AppCompatActivity
SignUpActivity --|> AppCompatActivity
PhoneLoginActivity --|> AppCompatActivity
OTPVerificationActivity --|> AppCompatActivity
AdminLoginActivity --|> AppCompatActivity
LoginSignUpActivity --|> AppCompatActivity
ForgotPasswordActivity --|> AppCompatActivity
ResetPasswordOTPActivity --|> AppCompatActivity
CreateNewPasswordActivity --|> AppCompatActivity
PasswordResetSuccessActivity --|> AppCompatActivity
LoginSuccessActivity --|> AppCompatActivity
LoginUnsuccessActivity --|> AppCompatActivity

PhoneLoginActivity --> FirebaseErrorHandler : uses
PhoneLoginActivity --> SessionManager : uses
OTPVerificationActivity --> FirebaseErrorHandler : uses
OTPVerificationActivity --> SessionManager : uses
AdminLoginActivity --> FirebaseErrorHandler : uses
AdminLoginActivity --> SessionManager : uses
ForgotPasswordActivity --> FirebaseErrorHandler : uses

' =============================================
' MAIN FEATURE ACTIVITIES
' =============================================
package "main_features" #DDFFFF {
    
    class DashboardActivity {
        - {static} TAG : String = "DashboardActivity"
        - mAuth : FirebaseAuth
        - sessionManager : SessionManager
        - btnLogout : ImageView
        - cardPathology : MaterialCardView
        - cardRadiology : MaterialCardView
        --
        # onCreate(Bundle)
        - navigateToLogin()
        - showLogoutDialog()
        - performLogout()
        - setupServiceCards()
        - setupBottomNavigation()
    }
    
    class ReportsActivity {
        - reportsRecyclerView : RecyclerView
        - emptyStateLayout : LinearLayout
        - adapter : PathologyReportAdapter
        - reportsList : List<PathologyReport>
        - allReports : List<PathologyReport>
        - currentFilter : String = "All"
        - btnFilterAll : MaterialButton
        - btnFilterPathology : MaterialButton
        - btnFilterRadiology : MaterialButton
        --
        # onCreate(Bundle)
        - loadReports()
        - setupBottomNavigation()
    }
    
    class DevicesActivity {
        --
        # onCreate(Bundle)
        - setupBottomNavigation()
    }
}

DashboardActivity --|> AppCompatActivity
ReportsActivity --|> AppCompatActivity
DevicesActivity --|> AppCompatActivity

DashboardActivity --> SessionManager : uses
DashboardActivity --> FirebaseErrorHandler : uses
ReportsActivity --> PathologyReportAdapter : uses
ReportsActivity --> PathologyReport : manages

' =============================================
' PATHOLOGY MODULE
' =============================================
package "pathology" #FFE4DD {
    
    class PathologyActivity {
        - reportsRecyclerView : RecyclerView
        - emptyStateLayout : LinearLayout
        - adapter : PathologyReportAdapter
        - reportsList : List<PathologyReport>
        - currentCategory : String = "All"
        - fabAddReport : FloatingActionButton
        --
        # onCreate(Bundle)
        # onResume()
        - loadReports()
        - setupCategoryTabs()
        - setupBottomNavigation()
    }
    
    class UploadPathologyReportActivity {
        - etLabName : TextInputEditText
        - actvTestName : AutoCompleteTextView
        - etSampleDate : TextInputEditText
        - etDoctorName : TextInputEditText
        - etPatientName : TextInputEditText
        - btnNext : MaterialButton
        - btnBack : ImageView
        --
        # onCreate(Bundle)
        - setupTestNameDropdown()
        - setupDatePicker()
        - validateFields() : boolean
    }
    
    class SelectReportSourceActivity {
        - cardTakePhoto : MaterialCardView
        - cardUploadFile : MaterialCardView
        - btnBack : ImageView
        - photoUri : Uri
        - labName, testName, sampleDate, doctorName, patientName : String
        - cameraLauncher : ActivityResultLauncher<Intent>
        - fileLauncher : ActivityResultLauncher<Intent>
        --
        # onCreate(Bundle)
        - checkCameraPermission() : boolean
        - requestCameraPermission()
        - openCamera()
        - openFilePicker()
        - openReportPreview(Uri)
    }
    
    class ReportPreviewActivity {
        - ivReportPreview : ImageView
        - btnBack : ImageView
        - btnSave : MaterialButton
        - labName, testName, sampleDate, doctorName, patientName : String
        - fileUri : Uri
        --
        # onCreate(Bundle)
        - saveReport()
        - determineCategory(String) : String
    }
    
    class ReportDetailActivity {
        - ivReportDetail : ImageView
        - btnBack, btnShare, btnDownload : ImageView
        - btnSyncABHA : MaterialButton
        - labName, testName, sampleDate, doctorName, patientName : String
        - fileUri : Uri
        --
        # onCreate(Bundle)
        - shareReport()
        - downloadReport()
        - syncWithABHA()
    }
}

PathologyActivity --|> AppCompatActivity
UploadPathologyReportActivity --|> AppCompatActivity
SelectReportSourceActivity --|> AppCompatActivity
ReportPreviewActivity --|> AppCompatActivity
ReportDetailActivity --|> AppCompatActivity

PathologyActivity --> PathologyReportAdapter : uses
PathologyActivity --> PathologyReport : manages
ReportPreviewActivity --> PathologyReport : creates

' =============================================
' RADIOLOGY MODULE
' =============================================
package "radiology" #E4DDFF {
    
    class RadiologyActivity {
        - fabAddReport : FloatingActionButton
        - btnBack : ImageView
        --
        # onCreate(Bundle)
        - setupBottomNavigation()
    }
    
    class UploadRadiologyReportActivity {
        - etCenterName : TextInputEditText
        - etScanType : AutoCompleteTextView
        - etScanDate : TextInputEditText
        - etDoctorName : TextInputEditText
        - etPatientName : TextInputEditText
        - calendar : Calendar
        - btnNext : MaterialButton
        - btnBack : ImageView
        --
        # onCreate(Bundle)
        - setupScanTypeDropdown()
        - setupDatePicker()
        - validateFields() : boolean
    }
    
    class SelectRadiologySourceActivity {
        --
        # onCreate(Bundle)
    }
    
    class RadiologyDetailActivity {
        --
        # onCreate(Bundle)
    }
    
    class RadiologyPreviewActivity {
        --
        # onCreate(Bundle)
    }
}

RadiologyActivity --|> AppCompatActivity
UploadRadiologyReportActivity --|> AppCompatActivity
SelectRadiologySourceActivity --|> AppCompatActivity
RadiologyDetailActivity --|> AppCompatActivity
RadiologyPreviewActivity --|> AppCompatActivity

' =============================================
' BMI MODULE
' =============================================
package "bmi" #DDE4FF {
    
    class BMIActivity {
        --
        # onCreate(Bundle)
    }
    
    class BMIGenderActivity {
        - selectedGender : String = "male"
        - maleBtn : View
        - femaleBtn : View
        - nextBtn : View
        - backBtn : View
        --
        # onCreate(Bundle)
        - setupBottomNavigation()
    }
    
    class BMIInputActivity {
        - etHeight : TextInputEditText
        - etWeight : TextInputEditText
        - etAge : TextInputEditText
        - gender : String
        --
        # onCreate(Bundle)
        - calculateBMI()
        - setupBottomNavigation()
    }
    
    class BMIResultActivity {
        - bmi : double
        - tvBmiValue : TextView
        - tvBmiCategory : TextView
        - btnSaveResult : View
        - backBtn : View
        --
        # onCreate(Bundle)
        - getBMICategory(double) : String
        - setupBottomNavigation()
    }
}

BMIActivity --|> AppCompatActivity
BMIGenderActivity --|> AppCompatActivity
BMIInputActivity --|> AppCompatActivity
BMIResultActivity --|> AppCompatActivity

BMIActivity --> BMIGenderActivity : navigates
BMIGenderActivity --> BMIInputActivity : navigates
BMIInputActivity --> BMIResultActivity : navigates

' =============================================
' ABHA MODULE
' =============================================
package "abha" #FFDDE4 {
    
    class ABHAActivity {
        - sharedPreferences : SharedPreferences
        - btnStartAbhaFlow : MaterialButton
        - {static} PREF_NAME : String = "medicam_pref"
        - {static} KEY_ABHA_REGISTERED : String = "abha_registered"
        --
        # onCreate(Bundle)
        - checkABHARegistrationStatus()
        - setupBottomNavigation()
    }
    
    class AbhaRegistrationActivity {
        - createButton : View
        - linkButton : View
        - connectLater : View
        --
        # onCreate(Bundle)
    }
    
    class AbhaVerifyAadhaarOtpActivity {
        --
        # onCreate(Bundle)
    }
    
    class AbhaVerifyMobileOtpActivity {
        --
        # onCreate(Bundle)
    }
    
    class AbhaLinkMobileActivity {
        --
        # onCreate(Bundle)
    }
    
    class AbhaChooseUsernameActivity {
        --
        # onCreate(Bundle)
    }
    
    class AbhaCreateProfileActivity {
        --
        # onCreate(Bundle)
    }
    
    class AbhaSuccessActivity {
        --
        # onCreate(Bundle)
    }
    
    class ABHACardDisplayActivity {
        - btnBack : ImageView
        - tabCard, tabConsent, tabProvider : MaterialButton
        - btnViewProfile, btnLogoutAbha, btnDownload : MaterialButton
        - cardContent : View
        - tabsLayout : LinearLayout
        - tabIndicator : View
        --
        # onCreate(Bundle)
        - initializeViews()
        - setupTabClickListeners()
        - setupButtonClickListeners()
        - setTabActive(MaterialButton)
        - showCardContent()
        - showConsentContent()
        - showProviderContent()
    }
}

ABHAActivity --|> AppCompatActivity
AbhaRegistrationActivity --|> AppCompatActivity
AbhaVerifyAadhaarOtpActivity --|> AppCompatActivity
AbhaVerifyMobileOtpActivity --|> AppCompatActivity
AbhaLinkMobileActivity --|> AppCompatActivity
AbhaChooseUsernameActivity --|> AppCompatActivity
AbhaCreateProfileActivity --|> AppCompatActivity
AbhaSuccessActivity --|> AppCompatActivity
ABHACardDisplayActivity --|> AppCompatActivity

ABHAActivity --> AbhaRegistrationActivity : new user
ABHAActivity --> ABHACardDisplayActivity : registered user
AbhaRegistrationActivity --> AbhaCreateProfileActivity : create new
AbhaRegistrationActivity --> AbhaLinkMobileActivity : link existing

' =============================================
' NAVIGATION RELATIONSHIPS
' =============================================

DashboardActivity --> PathologyActivity : navigates
DashboardActivity --> RadiologyActivity : navigates
DashboardActivity --> ABHAActivity : navigates
DashboardActivity --> BMIActivity : navigates
DashboardActivity --> DevicesActivity : navigates
DashboardActivity --> ReportsActivity : navigates

SplashActivity --> MainActivity : after 3 seconds

MainActivity --> SignUpActivity : navigates
MainActivity --> PhoneLoginActivity : navigates

PhoneLoginActivity --> OTPVerificationActivity : sends OTP
PhoneLoginActivity --> AdminLoginActivity : admin portal

AdminLoginActivity --> ForgotPasswordActivity : forgot password
AdminLoginActivity --> LoginSignUpActivity : sign up
AdminLoginActivity --> DashboardActivity : login success

OTPVerificationActivity --> DashboardActivity : verification success

@enduml
```

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    MediCam App                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌──────────────────────────────────────────────────────────────────────────────┐   │
│  │                           PRESENTATION LAYER                                  │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │   │
│  │  │ Activities  │  │  Adapters   │  │   Views     │  │  Bottom Navigation  │  │   │
│  │  │             │  │             │  │             │  │                     │  │   │
│  │  │ • Dashboard │  │ • Pathology │  │ • XML       │  │ • Home              │  │   │
│  │  │ • Pathology │  │   Report    │  │   Layouts   │  │ • Pathology         │  │   │
│  │  │ • Radiology │  │   Adapter   │  │ • Custom    │  │ • ABHA              │  │   │
│  │  │ • BMI       │  │             │  │   Views     │  │ • BMI               │  │   │
│  │  │ • ABHA      │  │             │  │             │  │ • Devices           │  │   │
│  │  │ • Devices   │  │             │  │             │  │                     │  │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────────────────────────┘   │
│                                         │                                            │
│  ┌──────────────────────────────────────▼───────────────────────────────────────┐   │
│  │                            UTILITY LAYER                                      │   │
│  │  ┌─────────────────────────┐  ┌──────────────────────────────────────────┐   │   │
│  │  │     SessionManager      │  │         FirebaseErrorHandler             │   │   │
│  │  │  (Singleton Pattern)    │  │          (Utility Pattern)               │   │   │
│  │  │                         │  │                                          │   │   │
│  │  │  • User Session         │  │  • Auth Error Messages                   │   │   │
│  │  │  • Auth Tokens          │  │  • Firestore Error Messages              │   │   │
│  │  │  • Encrypted Storage    │  │  • Exception Logging                     │   │   │
│  │  │  • ABHA Number          │  │  • Network Error Detection               │   │   │
│  │  │  • Session Validation   │  │                                          │   │   │
│  │  └─────────────────────────┘  └──────────────────────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────────────────────┘   │
│                                         │                                            │
│  ┌──────────────────────────────────────▼───────────────────────────────────────┐   │
│  │                             MODEL LAYER                                       │   │
│  │  ┌─────────────────────────┐  ┌──────────────────────────────────────────┐   │   │
│  │  │    PathologyReport      │  │            (Future Models)               │   │   │
│  │  │                         │  │                                          │   │   │
│  │  │  • labName              │  │  • RadiologyReport                       │   │   │
│  │  │  • testName             │  │  • BMIRecord                             │   │   │
│  │  │  • collectionDate       │  │  • ABHAProfile                           │   │   │
│  │  │  • doctorName           │  │  • DeviceConnection                      │   │   │
│  │  │  • patientName          │  │                                          │   │   │
│  │  │  • reportImageUri       │  │                                          │   │   │
│  │  │  • category             │  │                                          │   │   │
│  │  └─────────────────────────┘  └──────────────────────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────────────────────┘   │
│                                         │                                            │
│  ┌──────────────────────────────────────▼───────────────────────────────────────┐   │
│  │                           DATA/BACKEND LAYER                                  │   │
│  │  ┌─────────────────────────┐  ┌──────────────────────────────────────────┐   │   │
│  │  │     Firebase Auth       │  │         SharedPreferences                │   │   │
│  │  │                         │  │    (EncryptedSharedPreferences)          │   │   │
│  │  │  • Phone Auth           │  │                                          │   │   │
│  │  │  • Email Auth           │  │  • User Sessions                         │   │   │
│  │  │  • OTP Verification     │  │  • Report Storage (Gson)                 │   │   │
│  │  └─────────────────────────┘  │  • ABHA Status                           │   │   │
│  │                               └──────────────────────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Navigation Flow Diagram

```
                                    ┌─────────────────┐
                                    │  SplashActivity │
                                    │  (3 sec delay)  │
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │  MainActivity   │
                                    │   (Welcome)     │
                                    └───────┬─────────┘
                                            │
               ┌────────────────────────────┼────────────────────────────┐
               ▼                            ▼                            ▼
      ┌─────────────┐              ┌─────────────────┐          ┌─────────────┐
      │  SignUp     │              │  PhoneLogin     │          │ AdminLogin  │
      │  Activity   │              │   Activity      │          │  Activity   │
      └──────┬──────┘              └────────┬────────┘          └──────┬──────┘
             │                              │                          │
             │                              ▼                          │
             │                     ┌─────────────────┐                 │
             │                     │ OTPVerification │                 │
             │                     │    Activity     │                 │
             │                     └────────┬────────┘                 │
             │                              │                          │
             └──────────────────────────────┼──────────────────────────┘
                                            ▼
                                   ┌─────────────────┐
                                   │ DashboardActivity│
                                   │   (Main Hub)    │
                                   └────────┬────────┘
                                            │
         ┌──────────────┬───────────────────┼───────────────────┬──────────────┐
         │              │                   │                   │              │
         ▼              ▼                   ▼                   ▼              ▼
┌─────────────┐ ┌─────────────┐   ┌─────────────┐   ┌─────────────┐ ┌─────────────┐
│  PATHOLOGY  │ │  RADIOLOGY  │   │    BMI      │   │    ABHA     │ │   DEVICES   │
│   MODULE    │ │   MODULE    │   │   MODULE    │   │   MODULE    │ │   MODULE    │
└──────┬──────┘ └──────┬──────┘   └──────┬──────┘   └──────┬──────┘ └─────────────┘
       │               │                 │                 │
       ▼               ▼                 ▼                 ▼
┌─────────────┐ ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│ Pathology   │ │ Radiology   │   │ BMIGender   │   │ AbhaRegist- │
│  Activity   │ │  Activity   │   │  Activity   │   │   ration    │
└──────┬──────┘ └──────┬──────┘   └──────┬──────┘   └──────┬──────┘
       │               │                 │                 │
       ▼               ▼                 ▼          ┌──────┴──────┐
┌─────────────┐ ┌─────────────┐   ┌─────────────┐   ▼             ▼
│ Upload      │ │ Upload      │   │ BMIInput    │ ┌───────┐ ┌───────┐
│ Pathology   │ │ Radiology   │   │  Activity   │ │Create │ │ Link  │
│ Report      │ │ Report      │   └──────┬──────┘ │Profile│ │Mobile │
└──────┬──────┘ └──────┬──────┘          │        └───┬───┘ └───────┘
       │               │                 ▼            │
       ▼               ▼          ┌─────────────┐     ▼
┌─────────────┐ ┌─────────────┐   │ BMIResult   │ ┌───────────┐
│ Select      │ │ Select      │   │  Activity   │ │AbhaSuccess│
│ Source      │ │ Radiology   │   └─────────────┘ └─────┬─────┘
└──────┬──────┘ │ Source      │                         │
       │        └─────────────┘                         ▼
       ▼                                         ┌───────────┐
┌─────────────┐                                  │ ABHACard  │
│ Report      │                                  │ Display   │
│ Preview     │                                  └───────────┘
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Report      │
│ Detail      │
└─────────────┘
```

---

## Module Summary

| Module | Activities | Description |
|--------|------------|-------------|
| **Authentication** | `MainActivity`, `SplashActivity`, `SignUpActivity`, `PhoneLoginActivity`, `OTPVerificationActivity`, `AdminLoginActivity`, `LoginSignUpActivity`, `ForgotPasswordActivity`, `ResetPasswordOTPActivity`, `CreateNewPasswordActivity`, `PasswordResetSuccessActivity`, `LoginSuccessActivity`, `LoginUnsuccessActivity` | User authentication with Phone OTP and Email/Password |
| **Dashboard** | `DashboardActivity` | Main navigation hub after login |
| **Pathology** | `PathologyActivity`, `UploadPathologyReportActivity`, `SelectReportSourceActivity`, `ReportPreviewActivity`, `ReportDetailActivity` | Upload and manage pathology lab reports |
| **Radiology** | `RadiologyActivity`, `UploadRadiologyReportActivity`, `SelectRadiologySourceActivity`, `RadiologyDetailActivity`, `RadiologyPreviewActivity` | Upload and manage radiology scans |
| **BMI** | `BMIActivity`, `BMIGenderActivity`, `BMIInputActivity`, `BMIResultActivity` | BMI calculation workflow |
| **ABHA** | `ABHAActivity`, `AbhaRegistrationActivity`, `AbhaVerifyAadhaarOtpActivity`, `AbhaVerifyMobileOtpActivity`, `AbhaLinkMobileActivity`, `AbhaChooseUsernameActivity`, `AbhaCreateProfileActivity`, `AbhaSuccessActivity`, `ABHACardDisplayActivity` | ABHA Health ID registration and display |
| **Reports** | `ReportsActivity` | Unified view of all reports |
| **Devices** | `DevicesActivity` | Connected medical devices |
| **Utils** | `SessionManager`, `FirebaseErrorHandler` | Shared utility classes |
| **Models** | `PathologyReport` | Data models |
| **Adapters** | `PathologyReportAdapter`, `ReportViewHolder` | RecyclerView adapters |

---

## Design Patterns Used

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Singleton** | `SessionManager.getInstance(Context)` | Single instance for encrypted session management |
| **Utility Class** | `FirebaseErrorHandler` (private constructor) | Static methods for Firebase error handling |
| **Adapter** | `PathologyReportAdapter` | RecyclerView data binding with filtering |
| **ViewHolder** | `ReportViewHolder` | Efficient view recycling |
| **Builder** | `MasterKey.Builder`, `AlertDialog.Builder`, `PhoneAuthOptions.Builder` | Complex object construction |
| **Callback** | `OnVerificationStateChangedCallbacks` | Firebase phone auth events |
| **Observer** | Bottom Navigation click listeners | Event-driven navigation |

---

## Key Dependencies

| Dependency | Purpose |
|------------|---------|
| **Firebase Auth** | Phone OTP and Email/Password authentication |
| **AndroidX Security Crypto** | EncryptedSharedPreferences for secure storage |
| **Material Design Components** | MaterialButton, MaterialCardView, TextInputEditText |
| **Gson** | JSON serialization for report storage |
| **RecyclerView** | Efficient list displays |
| **ActivityResultContracts** | Camera and file picker results |
| **FileProvider** | Secure file sharing |

---

## Total Class Count

| Category | Count |
|----------|-------|
| Activities | 41 |
| Utility Classes | 2 |
| Model Classes | 1 |
| Adapter Classes | 2 (including ViewHolder) |
| **Total** | **46** |
