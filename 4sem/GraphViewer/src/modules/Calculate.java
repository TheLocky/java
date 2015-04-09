package modules;

import core.*;

public class Calculate implements Module {
    private Approximation appr;

    @Override
    public Pack Request(Pack p) {
        Pack ans = new Pack();
        if (p != null) {
            double[][] tmpPoints = p.get("Points");
            Integer deg = p.get("Degree");
            if ((tmpPoints != null) && (deg != null))
                appr = new Approximation(Matrix2D.createHorizontalVector(tmpPoints[0]),
                        Matrix2D.createHorizontalVector(tmpPoints[1]), deg);
            //Integer index = p.get("changeIndex");
            //Double newVal = p.get("newValue");
        }

        if(appr != null) {
            /*
            if ((index != null) && (newVal != null)) {
                appr.changePoint(index, newVal);
            }
             */
            ans.add("Polynom", appr.clone());
        }

        return ans;
    }
}
