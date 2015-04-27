package Dreamer;

interface Performable extends java.io.Serializable { void perform(); }
interface ActorPerformable extends java.io.Serializable { void perform(Actor a); }