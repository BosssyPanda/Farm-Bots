import { getRunner } from "@/lib/runner";
import { FALLBACK_CATALOG } from "@/lib/types";

export const runtime = "nodejs";

export async function GET() {
  try {
    const catalog = await getRunner().catalog();
    return Response.json(catalog);
  } catch (error) {
    return Response.json({
      ...FALLBACK_CATALOG,
      error: error instanceof Error ? error.message : String(error),
    });
  }
}
