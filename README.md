# 🛡️ Empire Ecosystem Simulator

A grid-based, predator–prey simulation where five historical **empires** (British, Spanish, Roman, Persian, Amazonian) and **Civilians** interact on a 2D field with **time-of-day** and **dynamic weather** affecting behavior. It features a live **Swing** UI with population stats and an optional **soundtrack** that changes as different empires dominate. 

---

## 🚀 Features

* 🗺️ **Large grid world** with tunable size (default **120×80**) and per-species spawn probabilities. 
* ⏱️ **Time windows** change activity: e.g., British pause for **tea time (15:00–17:00)**; Spanish pause for **siesta (14:00–16:00)**; Romans pause for **baths (11:00–13:00)**.   
* 🌤️ **Weather system** with **SUNNY / RAINY / FOGGY / SNOWY / MODERATE** and concrete effects (e.g., Spanish attack radius doubles in sun; British act twice in rain; Amazonians “vanish” in fog; snow can skip moves).   
* 🎨 **Live Swing UI** shows step, time, weather, and population; Amazonians turn white under fog to visually “disappear.”  
* 🎵 **Dynamic music** changes with the leading empire and plays a “Win” track when one empire remains.  
* 🧪 **Deterministic runs** by default (shared RNG seed **1111**) for reproducible behavior. 

---

## 🛠️ How It Works

1. **Init & Populate.** `Simulator` builds the field (default **120×80**) and randomly populates it with soldiers and civilians using per-type creation probabilities.  
2. **Step Loop.** Each step updates:
   * **Weather** (changes every **15** steps).
   * **Each entity**: Empires may **recruit** civilians, **reproduce**, **hunt** enemies, or **move**; Civilians **move/reproduce**. Snow may cause an entity to **skip** moving.   
   * **Time** advances and the **UI** refreshes.
3. **Special rules.**
   * **British**: act twice in **rain**. **Spanish**: attack radius **2** in **sun**. **Amazonians**: “invisible” in **fog**. **Snow**: 50% chance to stay put (see report).    
5. **End condition.** When only one empire remains (field no longer viable), music switches to **Win**. 

---

## 📁 Project Structure

```
├── Amazonian.java        # Amazonian soldiers (prey; fog behavior) 
├── British.java          # British soldiers (predator; rain buff; tea time) 
├── Civilian.java         # Neutral population; can be recruited by empires
├── Counter.java          # Generic counter for stats in the UI
├── Empire.java           # Abstract empire behavior (conquer/recruit/reproduce) 
├── Field.java            # Grid world; placement, adjacency, viability
├── FieldStats.java       # Population counters and viability checks
├── Location.java         # Grid record (row, col)
├── MusicPlayer.java      # (Optional) soundtrack controller per leading empire
├── Persian.java          # Persian soldiers (prey; night inactivity)
├── Person.java           # Abstract person base class (alive, sex, age, mating)
├── Randomizer.java       # Shared RNG (seed 1111) and random sex
├── Roman.java            # Roman soldiers (predator/prey; bath time; male-only)
├── Simulator.java        # Main loop, weather/time, populate/reset, music
├── SimulatorView.java    # Swing UI with grid, stats, weather/time labels
├── Spanish.java          # Spanish soldiers (predator; sunny buff; siesta)
├── Time.java             # Sim time with hour/minute and increment
├── Weather.java          # Weather enum + change logic
├── ressources/           # .wav audio tracks used by MusicPlayer
└── assignment_3_report.pdf  # Design & rules overview (optional reading)
```

(See class and report docs for specific behaviors cited above.)   

---

## 🔧 Requirements

* **JDK 17+** (uses Java records & Swing).
* No third-party libraries (Swing/Audio are in the JDK).

---

## 🔑 Setup

1. **Clone the repository**

```bash
git clone https://github.com/yl-edr/predator-prey-simulator.git
cd predator-prey-simulator
```

2. **Soundtrack files (already bundled)**
   * The .wav tracks live in ./ressources/.
   * Ensure ressources/ is on the runtime classpath so MusicPlayer can load them.
   * If you change track names, update the filename mapping inside MusicPlayer.java.

---

## ▶️ Usage

### Run (terminal)

```bash
# compile
javac *.java

# launch
java Simulator
```

You’ll see a window with **time**, **step**, **weather**, and **population**. Let it run, or press close to exit. 

### Tuning the simulation

Adjust initial spawn probabilities (e.g., `BRITISH_CREATION_PROBABILITY`, `AMAZONIAN_CREATION_PROBABILITY`, etc.) or grid size (`DEFAULT_WIDTH/DEPTH`) inside `Simulator`. 

---

## 📜 License

This project is licensed under the MIT License — feel free to use and modify it. \
***Authors: Rom Steinberg and Yaal Edrey***
