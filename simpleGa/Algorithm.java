package simpleGa;

public class Algorithm {

    /* GA parameters */
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static final int tournamentSize = 5;
    private static final boolean elitism = false;

    /* Public methods */
    
    // Evolve a population
    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false);

        // Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismOffset;
        if (elitism) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        // Loop over the population size and create new individuals with
        // crossover
        for (int i = elitismOffset; i < pop.size(); i += 2) {

            // Select parents (choose method here)
            Individual parent1 = rouletteWheelSelection(pop);
            // Individual parent1 = stochasticUniversalSelection(pop);   // optional
            Individual parent2 = rouletteWheelSelection(pop);
            // Individual parent2 = stochasticUniversalSelection(pop);   // optional

            // Get TWO offspring
            Individual[] offspring = crossover(parent1, parent2);

            // Store first child
            newPopulation.saveIndividual(i, offspring[0]);

            // Store second child if space allows
            if (i + 1 < pop.size()) {
                newPopulation.saveIndividual(i + 1, offspring[1]);
            }
        }

        // Mutate population
        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    // Crossover individuals
    // Single-point crossover producing TWO offspring — return first child
   private static Individual[] crossover(Individual parent1, Individual parent2) {

        Individual child1 = new Individual();
        Individual child2 = new Individual();

        // Choose random crossover point (1 .. length-1)
        int crossoverPoint = (int) (Math.random() * (parent1.size() - 1)) + 1;

        for (int i = 0; i < parent1.size(); i++) {
            if (i < crossoverPoint) {
                child1.setGene(i, parent1.getGene(i));
                child2.setGene(i, parent2.getGene(i));
            } else {
                child1.setGene(i, parent2.getGene(i));
                child2.setGene(i, parent1.getGene(i));
            }
        }

        return new Individual[]{ child1, child2 };
    }


    // Mutate an individual
    private static void mutate(Individual indiv) {
        // Loop through genes
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= mutationRate) {
                // Create random gene
                byte gene = (byte) Math.round(Math.random());
                indiv.setGene(i, gene);
            }
        }
    }

    // Select individuals for crossover
    // private static Individual tournamentSelection(Population pop) {
    //     // Create a tournament population
    //     Population tournament = new Population(tournamentSize, false);
    //     // For each place in the tournament get a random individual
    //     for (int i = 0; i < tournamentSize; i++) {
    //         int randomId = (int) (Math.random() * pop.size());
    //         tournament.saveIndividual(i, pop.getIndividual(randomId));
    //     }
    //     // Get the fittest
    //     Individual fittest = tournament.getFittest();
    //     return fittest;
    // }

    // This did not work correctly
    // We are not properly iterating through the population to find the selected individual
    // Instead, we were returning the same individual if the random number was less than total fitness
    // private static Individual rouletteWheelSelection(Population pop) {
    //     int totalFitness = 0;
    //     for (int i = 0; i < pop.size(); i++) {
    //         totalFitness += pop.getIndividual(i).getFitness();
    //     }

    //     double rand = Math.random() * totalFitness;

    //     if (totalFitness >= rand) {
    //         return pop.getIndividual(i);
    //     }
        
    //     return pop.getIndividual(pop.size() - 1);
    // }

    // Roulette Wheel Selection
    private static Individual rouletteWheelSelection(Population pop) {
        int totalFitness = 0;
        for (int i = 0; i < pop.size(); i++) {
            totalFitness += pop.getIndividual(i).getFitness();
        }

        double rand = Math.random() * totalFitness;

        double runningSum = 0;
        for (int i = 0; i < pop.size(); i++) {
            runningSum += pop.getIndividual(i).getFitness();
            if (runningSum >= rand) {
                return pop.getIndividual(i);
            }
        }

        return pop.getIndividual(pop.size() - 1);
    }

}