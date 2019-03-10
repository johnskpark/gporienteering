package gporienteering.gp.io;

import ec.Fitness;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import gporienteering.decisionprocess.Policy;
import gporienteering.decisionprocess.policy.GPPolicy;
import gporienteering.gp.OrienteeringPrimitiveSet;
import gporienteering.gp.ReactiveGPHHProblem;
import gputils.LispUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A GP result is a class that stores the information read from an out.stat file produced by a GP run.
 * It includes
 *  - a list of solutions (best individuals), one per generation.
 *  - a list of training fitnesses, each for a solution.
 *  - a list of demo fitnesses, each for a solution.
 *  - a best solution according to the training fitness.
 *  - the training fitness of the best solution.
 *  - the demo fitness of the best solution.
 *  - the time statistics, i.e. the time spent for each generation.
 */

public class GPResult {
	private List<String> expressions;
	private List<Policy> solutions;
	private List<Fitness> trainFitnesses;
	private List<Fitness> testFitnesses;
	private String bestExpression;
	private Policy bestSolution;
	private Fitness bestTrainFitness;
	private Fitness bestTestFitness;
	private DescriptiveStatistics timeStat;

	public GPResult() {
		expressions = new ArrayList<>();
		solutions = new ArrayList<>();
		trainFitnesses = new ArrayList<>();
		testFitnesses = new ArrayList<>();
	}

	public List<String> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<String> expressions) {
		this.expressions = expressions;
	}

	public List<Policy> getSolutions() {
		return solutions;
	}

	public void setSolutions(List<Policy> solutions) {
		this.solutions = solutions;
	}

	public List<Fitness> getTrainFitnesses() {
		return trainFitnesses;
	}

	public void setTrainFitnesses(List<Fitness> trainFitnesses) {
		this.trainFitnesses = trainFitnesses;
	}

	public List<Fitness> getTestFitnesses() {
		return testFitnesses;
	}

	public void setTestFitnesses(List<Fitness> testFitnesses) {
		this.testFitnesses = testFitnesses;
	}

	public String getBestExpression() {
		return bestExpression;
	}

	public void setBestExpression(String bestExpression) {
		this.bestExpression = bestExpression;
	}

	public Policy getBestSolution() {
		return bestSolution;
	}

	public void setBestSolution(Policy bestSolution) {
		this.bestSolution = bestSolution;
	}

	public Fitness getBestTrainFitness() {
		return bestTrainFitness;
	}

	public void setBestTrainFitness(Fitness bestTrainFitness) {
		this.bestTrainFitness = bestTrainFitness;
	}

	public Fitness getBestTestFitness() {
		return bestTestFitness;
	}

	public void setBestTestFitness(Fitness bestTestFitness) {
		this.bestTestFitness = bestTestFitness;
	}

	public DescriptiveStatistics getTimeStat() {
		return timeStat;
	}

	public void setTimeStat(DescriptiveStatistics timeStat) {
		this.timeStat = timeStat;
	}

	public Policy getSolutionAtGen(int gen) {
		return solutions.get(gen);
	}

	public Fitness getTrainFitnessAtGen(int gen) {
		return trainFitnesses.get(gen);
	}

	public Fitness getTestFitnessAtGen(int gen) {
		return testFitnesses.get(gen);
	}

	public double getTimeAtGen(int gen) {
		return timeStat.getElement(gen);
	}

	public void addExpression(String expression) {
		expressions.add(expression);
	}

	public void addSolution(Policy solution) {
		solutions.add(solution);
	}

	public void addTrainFitness(Fitness fitness) {
		trainFitnesses.add(fitness);
	}

	public void addTestFitness(Fitness fitness) {
		testFitnesses.add(fitness);
	}

	public static GPResult readFromFile(File file,
			Problem problem,
			SolutionType solutionType,
			FitnessType fitnessType) {
		switch (solutionType) {
		case SIMPLE_SOLUTION:
			return readSimpleSolutionFromFile(file, problem, fitnessType);
		case MULTIOBJECTIVE_SOLUTION:
			return readMultiobjectiveSolutionFromFile(file, problem, fitnessType);
		default:
			return null;
		}
	}

	public static GPResult readSimpleSolutionFromFile(File file,
			Problem problem,
			FitnessType fitnessType) {
		ReactiveGPHHProblem prob = (ReactiveGPHHProblem)problem;

		GPResult result = new GPResult();

		String line;
		Fitness fitness = null;
		Policy solution = null;
		String expression = "";

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			while (!(line = br.readLine()).equals("Best Individual of Run:")) {
				if (line.startsWith("Generation")) {
					br.readLine();
					br.readLine();
					br.readLine();
					line = br.readLine();
					fitness = readFitnessFromLine(line, fitnessType);
					br.readLine();
					expression = br.readLine();

					expression = LispUtils.simplifyExpression(expression);
					result.addExpression(expression);

					Policy routingPolicy =
							new GPPolicy(prob.getPoolFilter(),
									LispUtils.parseExpression(expression,
											OrienteeringPrimitiveSet.wholePrimitiveSet()));

					result.addSolution(routingPolicy);
					result.addTrainFitness(fitness);
					result.addTestFitness((Fitness)fitness.clone());

					solution = routingPolicy;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set the best solution as the solution in the last generation
		result.setBestExpression(expression);
		result.setBestSolution(solution);
		result.setBestTrainFitness(fitness);
		result.setBestTestFitness((Fitness)fitness.clone());

		return result;
	}

	// Compared to the simple solution reader, this reader reads only the individuals from the final Pareto Front
	public static GPResult readMultiobjectiveSolutionFromFile(File file,
			Problem problem,
			FitnessType fitnessType) {
		ReactiveGPHHProblem prob = (ReactiveGPHHProblem)problem;

		GPResult result = new GPResult();

		String line;
		Fitness fitness = null;
		Policy solution = null;
		String expression = "";

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			// Ignore everything up to the final Pareto Front solution.
			do {
				line = br.readLine(); 
			} while (line != null && !line.startsWith("Pareto Front of Subpopulation")); 

			if (line == null) {
				throw new IOException("Reached the end of file before reading the Pareto Front");
			}

			// Start reading the individuals in the Pareto Front
			while ((line = br.readLine()) != null) {
				line = br.readLine();
				fitness = readFitnessFromLine(line, fitnessType);
				// Ignore all the fluff in between the fitness and the tree. 
				while (!line.startsWith("Tree")) {
					line = br.readLine();
				}
				expression = br.readLine();

				expression = LispUtils.simplifyExpression(expression);
				result.addExpression(expression);

				Policy routingPolicy =
						new GPPolicy(prob.getPoolFilter(),
								LispUtils.parseExpression(expression,
										OrienteeringPrimitiveSet.wholePrimitiveSet()));

				result.addSolution(routingPolicy);
				result.addTrainFitness(fitness);
				result.addTestFitness((Fitness)fitness.clone());

				solution = routingPolicy;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set the best solution as the solution in the last generation
		result.setBestExpression(expression);
		result.setBestSolution(solution);
		result.setBestTrainFitness(fitness);
		result.setBestTestFitness((Fitness)fitness.clone());

		return result;
	}

	private static Fitness readFitnessFromLine(String line,
			FitnessType fitnessType) {
		switch (fitnessType) {
		case SIMPLE_FITNESS:
			return readSimpleFitnessFromLine(line);
		case MULTIOBJECTIVE_FITNESS:
			return readMultiobjectiveFitnessFromLine(line);
		default:
			return null;
		}
	}

	private static Fitness readSimpleFitnessFromLine(String line) {
		String[] segments = line.split("\\[|\\]");
		double fitness = Double.valueOf(segments[1]);
		MultiObjectiveFitness f = new MultiObjectiveFitness();
		f.objectives = new double[1];
		f.objectives[0] = fitness;

		return f;
	}

	private static Fitness readMultiobjectiveFitnessFromLine(String line) {
		String trim = line.replaceAll("Fitness: \\[([^\\]]+)\\]", "$1");
		String[] split = trim.split("\\s+");
		MultiObjectiveFitness f = new MultiObjectiveFitness();
		double[] fitness = new double[split.length];
		for (int i = 0; i < fitness.length; i++) {
			fitness[i] = Double.parseDouble(split[i]);
		}

		f.objectives = fitness;
		return f;
	}

	public static DescriptiveStatistics readTimeFromFile(File file) {
		DescriptiveStatistics generationalTimeStat = new DescriptiveStatistics();

		String line;

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			br.readLine();
			while(true) {
				line = br.readLine();

				if (line == null)
					break;

				String[] commaSegments = line.split(",");
				generationalTimeStat.addValue(Double.valueOf(commaSegments[1]));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return generationalTimeStat;
	}
}
