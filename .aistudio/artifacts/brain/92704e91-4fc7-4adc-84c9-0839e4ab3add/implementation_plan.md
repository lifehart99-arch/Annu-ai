# Annu AI — Stage 3: Gemini Node Proxy & Secure Token Management

Annu AI is a futuristic personal AI companion designed with a dark cybernetic aesthetic (deep navy, electric cyan, violet, neon pink) speaking Hindi, Hinglish, and English with warmth and clarity. Stage 3 builds the secure client-to-backend pipeline: authenticating against the Node proxy, managing bearer tokens securely, interfacing with `/chat` and `/models`, exposing the interactive AI Brain configuration screen, and establishing the foundational architecture.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The backend proxy URL defaults to the configured Cloud Run instance (`https://ais-dev-6qnaxhcnos55scya24ymkm-785707029323.asia-northeast1.run.app`) while allowing seamless custom endpoint overrides with local fallback and instant connection validation in the AI Brain screen.

- **Confirmed Decision 1 (Token Security)**: Bearer tokens and session secrets are managed via an encrypted credentials vault in Android Keystore / DataStore preferences, never exposed in source code, crash reports, or UI logs.
- **Confirmed Decision 2 (Default Model & Fallback)**: The AI Brain defaults to `gemini-3.8-flash` with quick-switch toggles for low-latency `gemini-3.1-flash-lite` and deep reasoning `gemini-4-argon`, populated dynamically from the backend `/models` endpoint.
- **Confirmed Decision 3 (Zero Direct Key Leaks)**: Android client communicates exclusively with the authenticated Node proxy via Retrofit/OkHttp; the Google Gemini API key stays strictly on the backend.

---

### 1. Overview & Core Concept

- **What It Does**: Provides Annu with a resilient communication link to Gemini through the authenticated proxy backend. Users can chat with Annu in Hindi, Hinglish, or English, test and configure backend connectivity, inspect token usage and latencies, and tune Annu's AI Brain parameters (temperature, model, system persona).
- **Target Audience / Persona**: Users seeking a proactive, empathetic companion who understands bilingual nuances (Hinglish/Hindi/English) and provides real device controls, quick daily assistance, and intelligent conversation.
- **Key Value**: Delivers fast, secure, authenticated Gemini responses without shipping vulnerable API keys in the client APK, coupled with transparent connection diagnostics and clear error reporting.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. *App Launch & Home Screen*: User enters Annu AI's futuristic cockpit with an animated pulsing holographic AI core (cyan/violet glow), status badge ("Online • Proxy Connected"), quick action shortcuts (Health, Device, Brain, Voice), and chat input pill.
  2. *Interactive Chat Thread*: User sends queries in English/Hindi/Hinglish. Annu displays animated typing feedback, streams or loads responses with markdown/code formatting, and records token usage and model badges.
  3. *AI Brain & Connection Diagnostics*: Tapping the Brain icon navigates to the AI Brain screen. User can view the backend URL, tap "Test Connection & Ping", select available Gemini models fetched from `/models`, edit the system prompt, adjust temperature sliders, and manage/rotate bearer auth tokens with instant status feedback.
  4. *Error Recovery*: If the backend token is invalid, rate limit is reached, or network drops, Annu renders friendly, non-technical recovery banners (e.g., "Connection paused: rate limit reached. Retrying in 12s...") with manual retry buttons.

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Cybernetic elegance with dark futuristic vibes. Deep void backgrounds with bioluminescent cyan (`#00F0FF`), glowing violet (`#8B5CF6`), and radiant magenta (`#EC4899`) accents.
  - *Color Palette & Mood*:
    - Background: Void Black (`#070A12`) / Dark Navy Surface (`#0D1322`)
    - Surface Containers: Glassmorphic cards with translucent navy borders (`#151E34`)
    - Primary Accent: Neon Cyan (`#00F0FF`) for AI speech pills, core pulse, active status
    - Secondary Accent: Radiant Violet (`#8B5CF6`) for user bubbles and AI Brain dials
    - Error / Alert: Crimson Coral (`#FF4565`) for safety warnings and network issues
  - *Typography & Hierarchy*:
    - Monospace badges (`Roboto Mono` / Sans-serif medium) for model IDs, latencies, and token counters
    - Smooth modern sans-serif typography with comfortable line heights for conversational Hindi/English text
  - *Component Styling & Layout*:
    - Rounded interactive pills (50% radii) for quick prompts
    - Glowing orb canvas with multi-layered sine-wave harmonic pulsation
    - Edge-to-edge layout with full support for safe drawing and navigation bar insets

- **Interactive Feedback & Motion**:
  - Breathing ripple animation around Annu's holographic core driven by state (Idle, Thinking, Answering)
  - Smooth spring transitions between Home, Chat Thread, AI Brain, and Health views
  - Material 3 ripple indications on all interactive touchpoints (min 48dp)

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Secure Proxy Architecture over Direct SDK Key Injection**:
  - *Chosen Approach*: Retrofit client calls Node proxy endpoints (`/chat`, `/models`, `/auth/verify`) using Bearer token authentication.
  - *Why*: Eliminates security risks of APK decompilation exposing Gemini API keys, while enabling central rate limiting, audit logging, and dynamic model routing.
  - *Alternatives Considered*: Direct Firebase AI SDK or client BuildConfig API key (rejected due to explicit Stage 3 requirement: "The Gemini key must never be in the app").

- **Decision 2: Token Management & Credential Store**:
  - *Chosen Approach*: `TokenManager` backed by Jetpack DataStore / encrypted local preferences storing the session bearer token, with a pre-configured prototype development key and UI override options.
  - *Why*: Gives out-of-the-box working experience in prototype testing while allowing secure rotation and manual override from the AI Brain settings.

- **Decision 3: Architectural Foundation for Future Stages (4–8)**:
  - *Chosen Approach*: Clean MVVM with `ChatViewModel`, `BrainViewModel`, `ActionRegistry` allowlist parser, `DeviceActions` dispatcher, and `HealthModule` interfaces.
  - *Why*: Ensures Stage 3 delivers a complete working chat & brain experience while leaving clean extension points for Voice (Stage 4), Device Control (Stage 5), Media/Social (Stage 6), Health AI (Stage 7), and Room persistence (Stage 8).

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

```
┌─────────────────────────────────────────────────────────────┐
│                       ANNU AI Cockpit                       │
│  ┌─────────────────────────┐   ┌─────────────────────────┐  │
│  │   Animated AI Core UI   │   │     AI Brain Screen     │  │
│  │   (Harmonic Canvas Orb) │   │ (URL, Models, Temp, Sys)│  │
│  └───────────┬─────────────┘   └───────────┬─────────────┘  │
│              │                             │                │
│              ▼                             ▼                │
│       ChatViewModel                BrainViewModel           │
│              │                             │                │
│              └──────────────┬──────────────┘                │
│                             │                               │
│                             ▼                               │
│                   GeminiRepository                          │
│        ┌────────────────────┴────────────────────┐          │
│        ▼                                         ▼          │
│  TokenManager (Secure Store)            GeminiApiService    │
│  (Bearer Token / Dev Secret)            (Retrofit / OkHttp) │
└──────────────────────────────────────────────────┬──────────┘
                                                   │ HTTPS
                                                   ▼
                     ┌────────────────────────────────────────┐
                     │            Node Proxy Server           │
                     │  - Bearer Token Auth Verification      │
                     │  - GET  /models (gemini-3.8-flash, etc)│
                     │  - POST /chat (prompt, history, temp)  │
                     │  - Rate Limiting & Safety Filtering    │
                     └────────────────────────────────────────┘
```

- **Data Models & State**:
  - `ChatMessage(id, role, text, timestamp, model, tokenUsage, isError)`
  - `ChatUiState(messages, isThinking, currentInput, activeModel, errorBanner)`
  - `BrainUiState(backendUrl, authToken, availableModels, selectedModel, temperature, systemInstruction, pingStatus, tokenStats)`
  - `ProxyChatRequest(messages, model, temperature, systemInstruction)`
  - `ProxyChatResponse(reply, model, tokensUsed, latencyMs)`
  - `ModelListResponse(models: List<String>)`

- **Interactive Component & State Mapping**:
  - Chat input triggers `ChatViewModel.sendMessage()`, sets `isThinking = true`, and animates the AI core to pulse rapidly.
  - "Test Connection" button in Brain screen invokes `BrainViewModel.testConnection()`, measuring round-trip latency and validating the bearer token against `/models`.
  - Errors (401 Invalid Key, 429 Rate Limit, 503 Unavailable Model, network timeout) are mapped to typed sealed classes (`BrainError`) with actionable UI recovery prompts.
