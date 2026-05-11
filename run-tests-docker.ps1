# Executa os testes do projeto usando uma imagem Maven Docker
param()
Write-Host "Rodando mvn test dentro de um container Maven..."
docker run --rm -v ${PWD}:/workspace -w /workspace maven:3.9.4-eclipse-temurin-11 mvn -B test
