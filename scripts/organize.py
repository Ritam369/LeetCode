import json
import re
import shutil
from pathlib import Path

import requests


# --------------------------------------------------
# Configuration
# --------------------------------------------------

ROOT_DIR = Path(__file__).resolve().parent.parent

CONFIG_FILE = ROOT_DIR / "config" / "topics.json"
CACHE_FILE = ROOT_DIR / "data" / "problems.json"
README_FILE = ROOT_DIR / "README.md"

LEETCODE_GRAPHQL_URL = "https://leetcode.com/graphql"

HEADERS = {"Content-Type": "application/json", "User-Agent": "Mozilla/5.0"}


# --------------------------------------------------
# Load configuration
# --------------------------------------------------


def load_config():
    with CONFIG_FILE.open("r", encoding="utf-8") as file:
        return json.load(file)


# --------------------------------------------------
# Metadata cache
# --------------------------------------------------


def load_cache():
    if not CACHE_FILE.exists():
        return {}

    with CACHE_FILE.open("r", encoding="utf-8") as file:
        return json.load(file)


def save_cache(cache):
    CACHE_FILE.parent.mkdir(parents=True, exist_ok=True)

    with CACHE_FILE.open("w", encoding="utf-8") as file:
        json.dump(cache, file, indent=2, ensure_ascii=False)

    print(f"✓ Metadata cache updated: {CACHE_FILE}")


# --------------------------------------------------
# LeetCode metadata
# --------------------------------------------------


def fetch_leetcode_metadata(slug):
    query = """
    query questionData($titleSlug: String!) {
      question(titleSlug: $titleSlug) {
        questionFrontendId
        title
        titleSlug
        difficulty
        topicTags {
          name
          slug
        }
      }
    }
    """

    response = requests.post(
        LEETCODE_GRAPHQL_URL,
        json={"query": query, "variables": {"titleSlug": slug}},
        headers=HEADERS,
        timeout=20,
    )

    response.raise_for_status()

    payload = response.json()

    question = payload.get("data", {}).get("question")

    if not question:
        raise RuntimeError(f"Could not find LeetCode problem: {slug}")

    return question


# --------------------------------------------------
# Problem folder detection
# --------------------------------------------------

PROBLEM_FOLDER_PATTERN = re.compile(r"^(\d+)-(.+)$")


def extract_problem_slug(folder_name):
    """
    Convert:

        0704-binary-search

    into:

        binary-search
    """

    match = PROBLEM_FOLDER_PATTERN.match(folder_name)

    if not match:
        return None

    return match.group(2)


def is_problem_folder(path):
    if not path.is_dir():
        return False

    return bool(PROBLEM_FOLDER_PATTERN.match(path.name))


# --------------------------------------------------
# Topic mapping
# --------------------------------------------------


def get_recognized_topics(metadata, config):
    """
    Convert LeetCode topic tags into our controlled
    DSA topics.
    """

    topic_map = config["topics"]

    recognized_topics = []

    for tag in metadata.get("topicTags", []):
        tag_slug = tag["slug"]

        for topic_name, accepted_slugs in topic_map.items():
            if tag_slug in accepted_slugs:
                if topic_name not in recognized_topics:
                    recognized_topics.append(topic_name)

    return recognized_topics


def determine_primary_topic(recognized_topics, config):
    """
    Select the physical folder based on the configured
    priority order.
    """

    priority = config["primary_topic_priority"]

    for topic in priority:
        if topic in recognized_topics:
            return topic

    return "Other"


# --------------------------------------------------
# README generation
# --------------------------------------------------


def generate_readme(problems, config):
    lines = []

    lines.append("# LeetCode DSA Practice")
    lines.append("")
    lines.append(
        "My LeetCode solutions, automatically organized by fundamental DSA topics."
    )
    lines.append("")

    # ----------------------------------------------
    # Statistics
    # ----------------------------------------------

    total = len(problems)

    difficulty_counts = {"Easy": 0, "Medium": 0, "Hard": 0}

    for problem in problems:
        difficulty = problem["difficulty"]

        if difficulty in difficulty_counts:
            difficulty_counts[difficulty] += 1

    lines.append("## 📊 Progress")
    lines.append("")

    lines.append("| Difficulty | Solved |")
    lines.append("|---|---:|")
    lines.append(f"| Easy | {difficulty_counts['Easy']} |")
    lines.append(f"| Medium | {difficulty_counts['Medium']} |")
    lines.append(f"| Hard | {difficulty_counts['Hard']} |")
    lines.append(f"| **Total** | **{total}** |")
    lines.append("")

    # ----------------------------------------------
    # Topic grouping
    # ----------------------------------------------

    topic_groups = {}

    for problem in problems:
        for topic in problem["topics"]:
            topic_groups.setdefault(topic, []).append(problem)

    lines.append("## 📚 Problems by Topic")
    lines.append("")

    topic_order = config["primary_topic_priority"]

    for topic in topic_order:
        if topic not in topic_groups:
            continue

        lines.append(f"### {topic}")
        lines.append("")

        lines.append("| # | Problem | Difficulty | Solution |")
        lines.append("|---:|---|---|---|")

        topic_problems = sorted(topic_groups[topic], key=lambda p: int(p["number"]))

        for problem in topic_problems:
            relative_path = Path(problem["primary_topic"]) / problem["folder"]

            link = relative_path.as_posix()

            lines.append(
                f"| {problem['number']} | "
                f"{problem['title']} | "
                f"{problem['difficulty']} | "
                f"[Solution](./{link}) |"
            )

        lines.append("")

    return "\n".join(lines)


# --------------------------------------------------
# Main organizer
# --------------------------------------------------


def main():
    print("========================================")
    print("       LeetCode Repository Organizer")
    print("========================================")
    print()

    config = load_config()
    cache = load_cache()

    problems = []

    # ----------------------------------------------
    # Find all problem folders
    # ----------------------------------------------

    problem_folders = [path for path in ROOT_DIR.iterdir() if is_problem_folder(path)]

    # Also search inside topic directories
    for topic_dir in ROOT_DIR.iterdir():
        if not topic_dir.is_dir():
            continue

        if topic_dir.name in {".git", ".github", ".venv", "scripts", "config", "data"}:
            continue

        for path in topic_dir.iterdir():
            if is_problem_folder(path):
                problem_folders.append(path)

    # Remove duplicates
    problem_folders = list(dict.fromkeys(problem_folders))

    print(f"Found {len(problem_folders)} problem folder(s).")
    print()

    # ----------------------------------------------
    # Process each problem
    # ----------------------------------------------

    for folder in problem_folders:
        slug = extract_problem_slug(folder.name)

        if not slug:
            continue

        print(f"→ Processing: {folder.name}")

        # ------------------------------------------
        # Get metadata from cache or LeetCode
        # ------------------------------------------

        if slug in cache:
            metadata = cache[slug]

            print("  ✓ Using cached metadata")

        else:
            print("  ↓ Fetching metadata from LeetCode...")

            metadata = fetch_leetcode_metadata(slug)

            cache[slug] = metadata

            print("  ✓ Metadata fetched")

        # ------------------------------------------
        # Determine topics
        # ------------------------------------------

        topics = get_recognized_topics(metadata, config)

        if not topics:
            topics = ["Other"]

        primary_topic = determine_primary_topic(topics, config)

        print(f"  ✓ Topics: {', '.join(topics)}")

        print(f"  ✓ Primary topic: {primary_topic}")

        # ------------------------------------------
        # Move folder
        # ------------------------------------------

        destination_dir = ROOT_DIR / primary_topic
        destination_dir.mkdir(parents=True, exist_ok=True)

        destination = destination_dir / folder.name

        # Only move if not already there
        if folder.resolve() != destination.resolve():
            if destination.exists():
                print(f"  ⚠ Destination already exists: {destination}")

            else:
                shutil.move(str(folder), str(destination))

                print(f"  ✓ Moved → {primary_topic}/")

        # ------------------------------------------
        # Store information for README
        # ------------------------------------------

        problems.append(
            {
                "number": metadata["questionFrontendId"],
                "title": metadata["title"],
                "slug": metadata["titleSlug"],
                "difficulty": metadata["difficulty"],
                "topics": topics,
                "primary_topic": primary_topic,
                "folder": folder.name,
            }
        )

        print()

    # ----------------------------------------------
    # Save metadata cache
    # ----------------------------------------------

    save_cache(cache)

    # ----------------------------------------------
    # Generate README
    # ----------------------------------------------

    readme_content = generate_readme(problems, config)

    README_FILE.write_text(readme_content, encoding="utf-8")

    print("✓ README.md generated")
    print()

    print("========================================")
    print("              Complete")
    print("========================================")


if __name__ == "__main__":
    main()
