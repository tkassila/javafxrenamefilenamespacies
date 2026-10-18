[[ -s "/home/tk/.sdkman/bin/sdkman-init.sh" ]] && source "/home/tk/.sdkman/bin/sdkman-init.sh"
# sdk use java 11.0.22.fx-librca
sdk use java 21.0.5.fx-librca
export javafxexepath=/home/tk/.sdkman/candidates/java/21.0.5.fx-librca 
export JAVA_HOME=$javafxexepath
export PATH=$JAVA_HOME/bin:$PATH
DISPLAY=:0 java -jar ./javafxrenamefilenamespacies.jar
