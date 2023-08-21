package io.snyk.devrel.ctfdaccounthook.service;

public interface AliasService {

    String getAlias();
    String[] CYBER_SECURITY_DICT = new String[] {
        "adware",
        "anti-botnet",
        "anti-malware",
        "anti-phishing",
        "anti-virus",
        "attack-vector",
        "authentication",
        "backdoor",
        "banker-trojan",
        "denylist",
        "botnet",
        "byoc",
        "byod",
        "byol",
        "captcha",
        "clickjacking",
        "clientless",
        "cots",
        "cryptojacking",
        "cyberbullying",
        "cybersecurity",
        "dark-web",
        "data-breach",
        "data-integrity",
        "dlp",
        "data-theft",
        "ddos",
        "decryption",
        "dns",
        "encryption",
        "edr",
        "exploit",
        "fido",
        "firewall",
        "greylist",
        "hacker",
        "honeypot",
        "iam",
        "identity",
        "ioc",
        "insider-threat",
        "ips",
        "iot",
        "keylogger",
        "malvertising",
        "malware",
        "attack-mitre",
        "patch",
        "pen-testing",
        "phishing",
        "pii",
        "ransomware",
        "rdp",
        "risktool",
        "rootkit",
        "sandbox",
        "scareware",
        "secaas",
        "ssl",
        "soc",
        "siem",
        "sim-swapping",
        "sniffing",
        "soar",
        "spam",
        "spear-phishing",
        "spoofing",
        "spyware",
        "threat-hunting",
        "2fa",
        "virus",
        "vpn",
        "vulnerability",
        "waf",
        "white-hat",
        "allowlist",
        "zero-day",
        "zero-touch"
    };
    
    String[] COLORS_DICT = new String[] {
        "amaranth",
        "amber",
        "amethyst",
        "apricot",
        "aqua",
        "aquamarine",
        "azure",
        "beige",
        "black",
        "blue",
        "blush",
        "bronze",
        "brown",
        "chocolate",
        "coffee",
        "copper",
        "coral",
        "crimson",
        "cyan",
        "emerald",
        "fuchsia",
        "gold",
        "gray",
        "green",
        "harlequin",
        "indigo",
        "ivory",
        "jade",
        "lavender",
        "lime",
        "magenta",
        "maroon",
        "moccasin",
        "olive",
        "orange",
        "peach",
        "pink",
        "plum",
        "purple",
        "red",
        "rose",
        "salmon",
        "sapphire",
        "scarlet",
        "silver",
        "tan",
        "teal",
        "tomato",
        "turquoise",
        "violet",
        "white",
        "yellow"
    };
    
    String[] ADJECTIVES_DICT = new String[] {
        "able",
        "above",
        "absent",
        "absolute",
        "abstract",
        "abundant",
        "academic",
        "acceptable",
        "accepted",
        "accessible",
        "accurate",
        "accused",
        "active",
        "actual",
        "acute",
        "added",
        "additional",
        "adequate",
        "adjacent",
        "administrative",
        "adorable",
        "advanced",
        "adverse",
        "advisory",
        "aesthetic",
        "afraid",
        "aggregate",
        "aggressive",
        "agreeable",
        "agreed",
        "agricultural",
        "alert",
        "alive",
        "alleged",
        "allied",
        "alone",
        "alright",
        "alternative",
        "amateur",
        "amazing",
        "ambitious",
        "amused",
        "ancient",
        "angry",
        "annoyed",
        "annual",
        "anonymous",
        "anxious",
        "apparent",
        "applicable",
        "appropriate",
        "arbitrary",
        "architectural",
        "arrogant",
        "artificial",
        "artistic",
        "asleep",
        "assistant",
        "associated",
        "atomic",
        "attractive",
        "automatic",
        "autonomous",
        "available",
        "average",
        "awake",
        "aware",
        "back",
        "bad",
        "balanced",
        "bare",
        "basic",
        "beautiful",
        "beneficial",
        "better",
        "bewildered",
        "big",
        "binding",
        "biological",
        "bitter",
        "bizarre",
        "blank",
        "blind",
        "blonde",
        "blushing",
        "boiling",
        "bold",
        "bored",
        "boring",
        "brainy",
        "brave",
        "breakable",
        "breezy",
        "brief",
        "bright",
        "brilliant",
        "broad",
        "broken",
        "bumpy",
        "burning",
        "busy",
        "calm",
        "capable",
        "careful",
        "casual",
        "causal",
        "cautious",
        "central",
        "certain",
        "changing",
        "charming",
        "cheap",
        "cheerful",
        "chemical",
        "chief",
        "chilly",
        "chosen",
        "chronic",
        "circular",
        "civic",
        "civil",
        "civilian",
        "classic",
        "classical",
        "clean",
        "clear",
        "clever",
        "clinical",
        "close",
        "closed",
        "cloudy",
        "clumsy",
        "coastal",
        "cognitive",
        "coherent",
        "cold",
        "collective",
        "colonial",
        "colorful",
        "colossal",
        "colourful",
        "combined",
        "comfortable",
        "commercial",
        "common",
        "compact",
        "comparable",
        "comparative",
        "compatible",
        "competent",
        "competitive",
        "complete",
        "complex",
        "complicated",
        "compulsory",
        "conceptual",
        "concerned",
        "concrete",
        "condemned",
        "confident",
        "confidential",
        "confused",
        "conscious",
        "conservation",
        "considerable",
        "consistent",
        "constant",
        "content",
        "continental",
        "continued",
        "continuing",
        "continuous",
        "controlled",
        "convenient",
        "conventional",
        "convinced",
        "convincing",
        "cooing",
        "cool",
        "cooperative",
        "corporate",
        "correct",
        "costly",
        "courageous",
        "creative",
        "critical",
        "crooked",
        "crowded",
        "crucial",
        "cuddly",
        "cultural",
        "curious",
        "curly",
        "current",
        "curved",
        "cute",
        "daily",
        "damp",
        "dead",
        "deaf",
        "deafening",
        "dear",
        "decent",
        "decisive",
        "deep",
        "defensive",
        "definite",
        "deliberate",
        "delicate",
        "delicious",
        "delighted",
        "delightful",
        "dependent",
        "depressed",
        "desirable",
        "desperate",
        "detailed",
        "determined",
        "developed",
        "developing",
        "devoted",
        "different",
        "digital",
        "diplomatic",
        "direct",
        "disgusted",
        "distant",
        "distinct",
        "distinctive",
        "distinguished",
        "disturbed",
        "disturbing",
        "diverse",
        "divine",
        "dizzy",
        "domestic",
        "dominant",
        "double",
        "doubtful",
        "drab",
        "dramatic",
        "dreadful",
        "driving",
        "dry",
        "dual",
        "due",
        "dull",
        "dusty",
        "dutch",
        "dying",
        "dynamic",
        "eager",
        "early",
        "eastern",
        "easy",
        "economic",
        "educational",
        "eerie",
        "effective",
        "efficient",
        "elaborate",
        "elated",
        "elderly",
        "eldest",
        "electoral",
        "electric",
        "electrical",
        "electronic",
        "elegant",
        "eligible",
        "emotional",
        "empirical",
        "empty",
        "enchanting",
        "encouraging",
        "endless",
        "energetic",
        "enormous",
        "enthusiastic",
        "entire",
        "entitled",
        "envious",
        "environmental",
        "equal",
        "equivalent",
        "essential",
        "established",
        "estimated",
        "ethical",
        "eventual",
        "everyday",
        "evident",
        "evil",
        "evolutionary",
        "exact",
        "excellent",
        "exceptional",
        "excess",
        "excessive",
        "excited",
        "exciting",
        "exclusive",
        "existing",
        "exotic",
        "expected",
        "expensive",
        "experienced",
        "experimental",
        "explicit",
        "extended",
        "extensive",
        "external",
        "extra",
        "extraordinary",
        "extreme",
        "exuberant",
        "faint",
        "fair",
        "faithful",
        "familiar",
        "famous",
        "fancy",
        "fantastic",
        "far",
        "fascinating",
        "fashionable",
        "fast",
        "favourable",
        "favourite",
        "federal",
        "fellow",
        "few",
        "fierce",
        "filthy",
        "final",
        "financial",
        "fine",
        "firm",
        "fiscal",
        "fixed",
        "flaky",
        "flat",
        "flexible",
        "fluffy",
        "fluttering",
        "flying",
        "following",
        "fond",
        "foolish",
        "foreign",
        "formal",
        "formidable",
        "forthcoming",
        "fortunate",
        "forward",
        "fragile",
        "frail",
        "frantic",
        "free",
        "frequent",
        "fresh",
        "friendly",
        "frightened",
        "front",
        "frozen",
        "full",
        "fun",
        "functional",
        "fundamental",
        "funny",
        "furious",
        "future",
        "fuzzy",
        "gastric",
        "general",
        "generous",
        "genetic",
        "gentle",
        "genuine",
        "geographical",
        "giant",
        "gigantic",
        "given",
        "glad",
        "glamorous",
        "gleaming",
        "global",
        "glorious",
        "golden",
        "good",
        "gorgeous",
        "gothic",
        "governing",
        "graceful",
        "gradual",
        "grand",
        "grateful",
        "greasy",
        "great",
        "grieving",
        "grim",
        "gross",
        "grotesque",
        "growing",
        "grumpy",
        "guilty",
        "handsome",
        "happy",
        "hard",
        "harsh",
        "head",
        "healthy",
        "heavy",
        "helpful",
        "helpless",
        "hidden",
        "high",
        "hilarious",
        "hissing",
        "historic",
        "historical",
        "hollow",
        "holy",
        "homely",
        "hon",
        "honest",
        "horizontal",
        "horrible",
        "hostile",
        "hot",
        "huge",
        "human",
        "hungry",
        "hurt",
        "hushed",
        "husky",
        "icy",
        "ideal",
        "identical",
        "ideological",
        "ill",
        "illegal",
        "imaginative",
        "immediate",
        "immense",
        "imperial",
        "implicit",
        "important",
        "impossible",
        "impressed",
        "impressive",
        "improved",
        "inadequate",
        "inappropriate",
        "inc",
        "inclined",
        "increased",
        "increasing",
        "incredible",
        "independent",
        "indirect",
        "individual",
        "industrial",
        "inevitable",
        "influential",
        "informal",
        "inherent",
        "initial",
        "injured",
        "inland",
        "inner",
        "innocent",
        "innovative",
        "inquisitive",
        "instant",
        "institutional",
        "insufficient",
        "intact",
        "integral",
        "integrated",
        "intellectual",
        "intelligent",
        "intense",
        "intensive",
        "interested",
        "interesting",
        "interim",
        "interior",
        "intermediate",
        "internal",
        "international",
        "intimate",
        "invisible",
        "involved",
        "irrelevant",
        "isolated",
        "itchy",
        "jealous",
        "jittery",
        "joint",
        "jolly",
        "joyous",
        "judicial",
        "juicy",
        "junior",
        "just",
        "keen",
        "key",
        "kind",
        "known",
        "labour",
        "large",
        "late",
        "latin",
        "lazy",
        "leading",
        "left",
        "legal",
        "legislative",
        "legitimate",
        "lengthy",
        "lesser",
        "level",
        "lexical",
        "liable",
        "liberal",
        "light",
        "like",
        "likely",
        "limited",
        "linear",
        "linguistic",
        "liquid",
        "literary",
        "little",
        "live",
        "lively",
        "living",
        "local",
        "logical",
        "lonely",
        "long",
        "loose",
        "lost",
        "loud",
        "lovely",
        "low",
        "loyal",
        "ltd",
        "lucky",
        "mad",
        "magic",
        "magnetic",
        "magnificent",
        "main",
        "major",
        "mammoth",
        "managerial",
        "managing",
        "manual",
        "many",
        "marginal",
        "marine",
        "marked",
        "married",
        "marvellous",
        "marxist",
        "mass",
        "massive",
        "mathematical",
        "mature",
        "maximum",
        "mean",
        "meaningful",
        "mechanical",
        "medical",
        "medieval",
        "melodic",
        "melted",
        "mental",
        "mere",
        "metropolitan",
        "mid",
        "middle",
        "mighty",
        "mild",
        "military",
        "miniature",
        "minimal",
        "minimum",
        "ministerial",
        "minor",
        "miserable",
        "misleading",
        "missing",
        "misty",
        "mixed",
        "moaning",
        "mobile",
        "moderate",
        "modern",
        "modest",
        "molecular",
        "monetary",
        "monthly",
        "moral",
        "motionless",
        "muddy",
        "multiple",
        "mushy",
        "musical",
        "mute",
        "mutual",
        "mysterious",
        "naked",
        "narrow",
        "nasty",
        "national",
        "native",
        "natural",
        "naughty",
        "naval",
        "near",
        "nearby",
        "neat",
        "necessary",
        "negative",
        "neighbouring",
        "nervous",
        "net",
        "neutral",
        "new",
        "nice",
        "noble",
        "noisy",
        "normal",
        "northern",
        "nosy",
        "notable",
        "novel",
        "nuclear",
        "numerous",
        "nursing",
        "nutritious",
        "nutty",
        "obedient",
        "objective",
        "obliged",
        "obnoxious",
        "obvious",
        "occasional",
        "occupational",
        "odd",
        "official",
        "ok",
        "okay",
        "old",
        "olympic",
        "only",
        "open",
        "operational",
        "opposite",
        "optimistic",
        "oral",
        "ordinary",
        "organic",
        "organisational",
        "original",
        "orthodox",
        "other",
        "outdoor",
        "outer",
        "outrageous",
        "outside",
        "outstanding",
        "overall",
        "overseas",
        "overwhelming",
        "painful",
        "pale",
        "panicky",
        "parallel",
        "parental",
        "parliamentary",
        "partial",
        "particular",
        "passing",
        "passive",
        "past",
        "patient",
        "payable",
        "peaceful",
        "peculiar",
        "perfect",
        "permanent",
        "persistent",
        "personal",
        "petite",
        "philosophical",
        "physical",
        "plain",
        "planned",
        "plastic",
        "pleasant",
        "pleased",
        "poised",
        "polite",
        "political",
        "poor",
        "popular",
        "positive",
        "possible",
        "potential",
        "powerful",
        "practical",
        "precious",
        "precise",
        "preferred",
        "pregnant",
        "preliminary",
        "premier",
        "prepared",
        "present",
        "presidential",
        "pretty",
        "previous",
        "prickly",
        "primary",
        "prime",
        "primitive",
        "principal",
        "printed",
        "prior",
        "private",
        "probable",
        "productive",
        "professional",
        "profitable",
        "profound",
        "progressive",
        "prominent",
        "promising",
        "proper",
        "proposed",
        "prospective",
        "protective",
        "protestant",
        "proud",
        "provincial",
        "psychiatric",
        "psychological",
        "public",
        "puny",
        "pure",
        "purring",
        "puzzled",
        "quaint",
        "qualified",
        "quarrelsome",
        "querulous",
        "quick",
        "quickest",
        "quiet",
        "quintessential",
        "quixotic",
        "racial",
        "radical",
        "rainy",
        "random",
        "rapid",
        "rare",
        "raspy",
        "rational",
        "ratty",
        "raw",
        "ready",
        "real",
        "realistic",
        "rear",
        "reasonable",
        "recent",
        "reduced",
        "redundant",
        "regional",
        "registered",
        "regular",
        "regulatory",
        "related",
        "relative",
        "relaxed",
        "relevant",
        "reliable",
        "relieved",
        "religious",
        "reluctant",
        "remaining",
        "remarkable",
        "remote",
        "renewed",
        "representative",
        "repulsive",
        "required",
        "resident",
        "residential",
        "resonant",
        "respectable",
        "respective",
        "responsible",
        "resulting",
        "retail",
        "retired",
        "revolutionary",
        "rich",
        "ridiculous",
        "right",
        "rigid",
        "ripe",
        "rising",
        "rival",
        "roasted",
        "robust",
        "rolling",
        "romantic",
        "rotten",
        "rough",
        "round",
        "royal",
        "rubber",
        "rude",
        "ruling",
        "running",
        "rural",
        "sacred",
        "sad",
        "safe",
        "salty",
        "satisfactory",
        "satisfied",
        "scared",
        "scary",
        "scattered",
        "scientific",
        "scornful",
        "scrawny",
        "screeching",
        "secondary",
        "secret",
        "secure",
        "select",
        "selected",
        "selective",
        "selfish",
        "semantic",
        "senior",
        "sensible",
        "sensitive",
        "separate",
        "serious",
        "severe",
        "shaggy",
        "shaky",
        "shallow",
        "shared",
        "sharp",
        "sheer",
        "shiny",
        "shivering",
        "shocked",
        "short",
        "shrill",
        "shy",
        "sick",
        "significant",
        "silent",
        "silky",
        "silly",
        "similar",
        "simple",
        "single",
        "skilled",
        "skinny",
        "sleepy",
        "slight",
        "slim",
        "slimy",
        "slippery",
        "slow",
        "small",
        "smart",
        "smiling",
        "smoggy",
        "smooth",
        "social",
        "socialist",
        "soft",
        "solar",
        "sole",
        "solid",
        "sophisticated",
        "sore",
        "sorry",
        "sound",
        "sour",
        "southern",
        "soviet",
        "spare",
        "sparkling",
        "spatial",
        "special",
        "specific",
        "specified",
        "spectacular",
        "spicy",
        "spiritual",
        "splendid",
        "spontaneous",
        "sporting",
        "spotless",
        "spotty",
        "square",
        "squealing",
        "stable",
        "stale",
        "standard",
        "static",
        "statistical",
        "statutory",
        "steady",
        "steep",
        "sticky",
        "stiff",
        "still",
        "stingy",
        "stormy",
        "straight",
        "straightforward",
        "strange",
        "strategic",
        "strict",
        "striking",
        "striped",
        "strong",
        "structural",
        "stuck",
        "stupid",
        "subjective",
        "subsequent",
        "substantial",
        "subtle",
        "successful",
        "successive",
        "sudden",
        "sufficient",
        "suitable",
        "sunny",
        "super",
        "superb",
        "superior",
        "supporting",
        "supposed",
        "supreme",
        "sure",
        "surprised",
        "surprising",
        "surrounding",
        "surviving",
        "suspicious",
        "sweet",
        "swift",
        "symbolic",
        "sympathetic",
        "systematic",
        "tall",
        "tame",
        "tart",
        "tasteless",
        "tasty",
        "technical",
        "technological",
        "teenage",
        "temporary",
        "tender",
        "tense",
        "terrible",
        "territorial",
        "testy",
        "then",
        "theoretical",
        "thick",
        "thin",
        "thirsty",
        "thorough",
        "thoughtful",
        "thoughtless",
        "thundering",
        "tight",
        "tiny",
        "tired",
        "top",
        "tory",
        "total",
        "tough",
        "toxic",
        "traditional",
        "tragic",
        "tremendous",
        "tricky",
        "tropical",
        "troubled",
        "typical",
        "ugliest",
        "ugly",
        "ultimate",
        "unable",
        "unacceptable",
        "unaware",
        "uncertain",
        "unchanged",
        "uncomfortable",
        "unconscious",
        "underground",
        "underlying",
        "unemployed",
        "uneven",
        "unexpected",
        "unfair",
        "unfortunate",
        "unhappy",
        "uniform",
        "uninterested",
        "unique",
        "united",
        "universal",
        "unknown",
        "unlikely",
        "unnecessary",
        "unpleasant",
        "unsightly",
        "unusual",
        "unwilling",
        "upper",
        "upset",
        "uptight",
        "urban",
        "urgent",
        "used",
        "useful",
        "useless",
        "usual",
        "vague",
        "valid",
        "valuable",
        "variable",
        "varied",
        "various",
        "varying",
        "vast",
        "verbal",
        "vertical",
        "very",
        "vicarious",
        "vicious",
        "victorious",
        "violent",
        "visible",
        "visiting",
        "visual",
        "vital",
        "vitreous",
        "vivacious",
        "vivid",
        "vocal",
        "vocational",
        "voiceless",
        "voluminous",
        "voluntary",
        "vulnerable",
        "wandering",
        "warm",
        "wasteful",
        "watery",
        "weak",
        "wealthy",
        "weary",
        "wee",
        "weekly",
        "weird",
        "welcome",
        "well",
        "western",
        "wet",
        "whispering",
        "whole",
        "wicked",
        "wide",
        "widespread",
        "wild",
        "wilful",
        "willing",
        "willowy",
        "wily",
        "wise",
        "wispy",
        "wittering",
        "witty",
        "wonderful",
        "wooden",
        "working",
        "worldwide",
        "worried",
        "worrying",
        "worthwhile",
        "worthy",
        "written",
        "wrong",
        "xenacious",
        "xenial",
        "xenogeneic",
        "xenophobic",
        "xeric",
        "xerothermic",
        "yabbering",
        "yammering",
        "yappiest",
        "yappy",
        "yawning",
        "yearling",
        "yearning",
        "yeasty",
        "yelling",
        "yelping",
        "yielding",
        "yodelling",
        "young",
        "youngest",
        "youthful",
        "ytterbic",
        "yucky",
        "yummy",
        "zany",
        "zealous",
        "zeroth",
        "zestful",
        "zesty",
        "zippy",
        "zonal",
        "zoophagous",
        "zygomorphic",
        "zygotic"
    };
}
